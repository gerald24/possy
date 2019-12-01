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

import net.g24.possy.service.extensions.ResettableLazyManager
import net.g24.possy.service.extensions.resettableLazy
import net.g24.possy.service.model.PossyAvatar
import net.g24.possy.service.model.PossyIssue
import net.g24.possy.service.model.PossyProject
import net.g24.possy.service.model.PrintTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class JiraService(
        private val jiraConfigurationProperties: JiraConfigurationProperties,
        @Qualifier("jiraRestTemplate") private val jiraRestTemplate: RestTemplate,
        resettableLazyManager: ResettableLazyManager
) {

    companion object {
        private const val GET_PROJECTS = "/rest/api/2/project"
        private const val GET_ISSUES = "/rest/api/2/search?fields=%s&jql=%s&maxResults=%d"
    }

    val projects: List<PossyProject>? by resettableLazy(resettableLazyManager) {
        jiraProjects?.map {
            PossyProject(it.key, it.name, projectAvatar(it.avatarUrls.`48x48`))
        }
    }

    fun findIssues(project: String, jql: String): List<PossyIssue> {
        if (jql.isNullOrBlank()) {
            return emptyList()
        }
        var query = jql.trim()
        if (query.matches("\\d+".toRegex())) {
            query = "key=$project-$query"
        }
        return findIssuesForJql("project=$project AND $query")
    }

    private fun findIssuesForJql(jql: String) =
            extractAndConvertIssues(getIssues(jql, 50))


    fun loadRecentIssues(project: String) =
            extractAndConvertIssues(getIssues(String.format(jiraConfigurationProperties.jql.projectsRecentIssues!!, project), 20))


    private fun getIssues(jql: String, maxResults: Int): ResponseEntity<JqlIssueResult> {
        return jiraRestTemplate.getForEntity(
                getUrlForJql(jiraConfigurationProperties.jql.fields!!.joinToString(","), jql, maxResults),
                JqlIssueResult::class.java)
    }

    private fun getUrlForJql(fields: String, jql: String, maxResults: Int) = String.format(GET_ISSUES, fields, jql, maxResults)

    private fun extractAndConvertIssues(response: ResponseEntity<JqlIssueResult>): List<PossyIssue> {
        if (response.statusCode != HttpStatus.OK) {
            return emptyList()
        }

        val jiraIssues = response.body!!.issues
        val references = mutableMapOf<String, Map<String, Any?>?>()
        return jiraIssues.map { asPossyIssue(it, references) }
    }

    private fun asPossyIssue(issue: JiraIssue, references: MutableMap<String, Map<String, Any?>?>): PossyIssue {
        val referenceResolver: ReferenceResolver = object : ReferenceResolver {
            override fun resolve(key: String): Map<String, Any?>? {
                if (references.contains(key)) {
                    return references[key]
                }
                val result = getIssues("key=$key", 1)
                if (result.statusCode != HttpStatus.OK)
                    return null

                val jiraIssue = result.body!!.issues.firstOrNull() ?: return null
                references[key] = jiraIssue.allValues()
                return references[key]
            }
        }
        val weightValue = field(issue, jiraConfigurationProperties.mapping.weight!!, referenceResolver)
        val weight = if (weightValue is Double) {
            (weightValue as Double).toInt().toString()
        } else {
            weightValue?.toString()
        }

        return PossyIssue(
                template = getTemplate(field(issue, jiraConfigurationProperties.mapping.templateField!!, referenceResolver) as String),
                key = issue.key,
                weight = weight,
                tag = field(issue, jiraConfigurationProperties.mapping.tag!!, referenceResolver)?.toString(),
                content = (field(issue, jiraConfigurationProperties.mapping.content!!, referenceResolver) as String))
    }

    private fun getTemplate(value: String): PrintTemplate {
        if (jiraConfigurationProperties.mapping.bug!!.contains(value)) {
            return PrintTemplate.BUG
        }
        if (jiraConfigurationProperties.mapping.story!!.contains(value)) {
            return PrintTemplate.STORY
        }
        return PrintTemplate.TASK
    }

    private interface ReferenceResolver {
        fun resolve(key: String): Map<String, Any?>?
    }

    private fun field(issue: JiraIssue, path: String, referenceResolver: ReferenceResolver): Any? {
        return path.split("|")
                .stream()
                .map { p -> resolve(issue.allValues(), p.split(".").toMutableList(), referenceResolver) }
                .filter { it != null }
                .findFirst()
                .orElse(null)
    }

    private fun resolve(values: Map<String, Any?>?, components: MutableList<String>, referenceResolver: ReferenceResolver): Any? {
        try {
            if (values == null) {
                return null
            }
            val pathComponent = components.first()
            val isReference = pathComponent.endsWith("*")
            var valuesOrNull = values[pathComponent.removeSuffix("*")] ?: return null
            if (valuesOrNull is String && !valuesOrNull.isNullOrBlank() && isReference) {
                valuesOrNull = referenceResolver.resolve(valuesOrNull) ?: return null
            }
            return if (components.size > 1 && valuesOrNull is Map<*, *>) {
                resolve(valuesOrNull as Map<String, Any>, components.apply { removeAt(0) }, referenceResolver)
            } else {
                valuesOrNull
            }
        } catch (e: Exception) {
            throw RuntimeException("error resolve $components for $values")
        }
    }

    private val jiraProjects: List<JiraProject>? by resettableLazy(resettableLazyManager) {
        try {
            jiraRestTemplate.getForEntity(GET_PROJECTS, Array<JiraProject>::class.java)
                    .body!!
                    .sortedBy { it.key }
        } catch (e: Exception) {
            null
        }
    }

    private fun projectAvatar(url: String): PossyAvatar? {
        val result = jiraRestTemplate.getForEntity(url, ByteArray::class.java)
        val contentType = result.headers[HttpHeaders.CONTENT_TYPE]?.get(0) ?: ""
        if (result.statusCode == HttpStatus.OK) {
            if (contentType.startsWith("image/png")) {
                return PossyAvatar("png", result.body!!)
            }
            if (contentType.startsWith("image/svg+xml;charset=UTF-8")) {
                return PossyAvatar("svg", result.body!!)
            }
        }
        return null
    }

}
