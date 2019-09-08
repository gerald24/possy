package net.g24.possy.service.jira

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

@Component
@ConfigurationProperties(prefix = "jira")
@Validated
class JiraConfigurationProperties(
        var url: String? = null,
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
