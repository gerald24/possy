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

import net.g24.possy.service.model.PrintRequest
import net.g24.possy.service.model.PrintTemplate

data class AvatarUrls(val `48x48`: String, val `32x32`: String, val `24x24`: String, val `16x16`: String)

data class JiraProject(val key: String, val name: String, val avatarUrls: AvatarUrls)

class JiraProjectAvatar(val contentType: String, val content: ByteArray)

data class JiraIssueType(val id: String, val name: String)

data class JiraFields(
        var summary: String,
        var customfield_10102: Double? = null,
        val customfield_10105: String? = null,
        var issuetype: JiraIssueType) {
    val issuetypeId: String
        get() = issuetype.id

    val issuetypeName: String
        get() = issuetype.name
}

data class JiraIssue(val key: String, var fields: JiraFields) {

    // TODO https://github.com/gerald24/possy/issues/3
    val template: PrintTemplate
        get() {
            val id = issueTypeId
            if ("1" == id || "10200" == id || "10800" == id || "11105" == id) {
                return PrintTemplate.BUG
            }
            return if ("10101" == id || "10201" == id) {
                PrintTemplate.STORY
            } else PrintTemplate.TASK

        }

    val issueTypeId: String?
        get() = fields.issuetypeId

    val issueTypeName: String
        get() = fields.issuetypeName

    val summary: String
        get() = fields.summary

    val storyPoints: String?
        get() = fields.customfield_10102?.toInt()?.toString()

    val eposPoints: String?
        get() = fields.customfield_10105

    fun asPrintRequest(): PrintRequest {
        return PrintRequest(template, key, storyPoints, eposPoints, summary!!)
    }
}

data class JqlIssueResult(val issues: List<JiraIssue>)

