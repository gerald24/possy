/*
 * This file is part of possy.
 *
 * possy is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * possy is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with possy. If not, see <http://www.gnu.org/licenses/>.
 */
package net.g24.possy.service.jira


import net.g24.possy.service.configuration.JiraConfiguration
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.io.UnsupportedEncodingException
import java.util.*

// TODO find better solution for custom fields (e.g. story, storypoints)

/**
 * @author: Gerald Leeb
 */
@Component
class JiraService @Autowired constructor(jiraConfiguration: JiraConfiguration) {

    private val restTemplate: RestTemplate = RestTemplate()
    private val httpHeaders = createHeadersWithAuthentication(jiraConfiguration.username!!, jiraConfiguration.password!!)
    private val jiraBaseURL: String = StringUtils.removeEnd(jiraConfiguration.url, "/")

    companion object {
        private val GET_PROJECTS = "%s/project"
        private val STORY_POINTS_CUSTOM_FIELD = "customfield_10102"
        private val EPOS_POINTS_CUSTOM_FIELD = "customfield_10105"
        private val GET_ISSUES = "%s/search?fields=%s&jql=%s&maxResults=%d"
        private val RESULT_FIELDS = "issuetype,key,summary,$STORY_POINTS_CUSTOM_FIELD,$EPOS_POINTS_CUSTOM_FIELD"
        private val RECENT_ISSUES_FOR_PROJECT_JQL = "project=%s+AND+(created>=-1w+OR+updated>=-1w)+ORDER+BY+updated+DESC"
        private val ENCODING = "UTF-8"
    }

    val projects: List<JiraProject> by lazy {
        restTemplate.exchange(
                String.format(GET_PROJECTS, jiraBaseURL),
                HttpMethod.GET,
                HttpEntity<List<JiraProject>>(httpHeaders),
                object : ParameterizedTypeReference<List<JiraProject>>() {})
                .body
                .sortedBy { it.key }
    }

    val projectImages: Map<String, JiraProjectAvatar?> by lazy {
        projects
                .map { it.key to projectImage(it.avatarUrls.`48x48`) }
                .toMap()
    }

    private fun projectImage(url: String): JiraProjectAvatar? {
        val result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity<ByteArray>(httpHeaders),
                ByteArray::class.java)
        val contentType = result.headers[HttpHeaders.CONTENT_TYPE]?.get(0) ?: ""
        if (result.statusCode == HttpStatus.OK) {
            if (contentType.startsWith("image/png")) {
                return JiraProjectAvatar("png", result.body)
            }
            if (contentType.startsWith("image/svg+xml;charset=UTF-8")) {
                return JiraProjectAvatar("svg", result.body)
            }
        }
        return null
    }


    fun findIssues(project: String, jql: String): List<JiraIssue> {
        return findIssuesForJql("project = $project AND $jql")
    }

    private fun findIssuesForJql(jql: String) =
            extractIssues(getIssues(jql, 50))


    fun loadRecentIssues(project: String) =
            extractIssues(getIssues(String.format(RECENT_ISSUES_FOR_PROJECT_JQL, project), 20))


    private fun getIssues(jql: String, maxResults: Int): ResponseEntity<JqlIssueResult> {
        return restTemplate.exchange(
                getUrlForJql(RESULT_FIELDS, jql, maxResults),
                HttpMethod.GET,
                HttpEntity<JqlIssueResult>(httpHeaders),
                JqlIssueResult::class.java)
    }

    private fun getUrlForJql(fields: String, jql: String, maxResults: Int) =
            String.format(GET_ISSUES, jiraBaseURL, fields, jql, maxResults)


    private fun extractIssues(response: ResponseEntity<JqlIssueResult>) =
            if (response.statusCode != HttpStatus.OK) emptyList() else response.body!!.issues


    private fun createHeadersWithAuthentication(username: String, password: String) =
            HttpHeaders().apply {
                add("Authorization", "Basic " + getBase64Credentials(username, password))
            }

    private fun getBase64Credentials(username: String, password: String) =
            try {
                String(Base64.getEncoder().encode("$username:$password".toByteArray(charset(ENCODING))))
            } catch (e: UnsupportedEncodingException) {
                throw RuntimeException(e)
            }

}
