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

data class AvatarUrls(val `48x48`: String, val `32x32`: String, val `24x24`: String, val `16x16`: String)

data class JiraProject(val key: String, val name: String, val avatarUrls: AvatarUrls)

data class JiraIssue(val key: String, var fields: Map<String, Any> = mutableMapOf()) {
    fun allValues(): Map<String, Any?>? {
        val values = fields.toMutableMap()
        values["key"] = key
        return values.toMap()
    }
}

data class JqlIssueResult(val issues: List<JiraIssue>)

