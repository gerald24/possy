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
package net.g24.possy.service.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

@Component
@ConfigurationProperties(prefix = "jira")
@Validated
class JiraConfiguration(
        var url: String? = null,
        var browseUrl: String? = null,
        var username: String? = null,
        var password: String? = null,
        val jql: JQL = JQL(),
        val mapping: Mapping = Mapping()) {

    class JQL(
            var projectsRecentIssues: String? = null,
            var fields: List<String>? = emptyList())

    class Mapping(
            var content: String? = null,
            var weight: String? = null,
            var tag: String? = null,
            var templateField: String? = null,
            var bug: List<String>? = emptyList(),
            var story: List<String>? = emptyList())

}


