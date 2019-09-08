package net.g24.possy.service.jira

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Configuration
@ConfigurationProperties(prefix = "jira")
@Validated
class JiraConfigurationProperties {

    lateinit var url: String
    lateinit var username: String
    lateinit var password: String
    lateinit var projects: String
}
