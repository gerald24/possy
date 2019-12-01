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

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import net.g24.possy.service.model.PossyAvatar
import net.g24.possy.service.model.PrintTemplate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate

@SpringBootTest
@ExtendWith(SpringExtension::class)
class JiraServiceTest {

    @Qualifier("jiraRestTemplate")
    @MockkBean
    private lateinit var jiraRestTemplate: RestTemplate

    @Autowired
    private lateinit var jiraService: JiraService

    @Test
    fun `correct issue mapping`() {
        every { jiraRestTemplate.getForEntity(any<String>(), JqlIssueResult::class.java) } returns ResponseEntity.ok(JqlIssueResult(listOf(
                JiraIssue(key = "STORY-1", fields = mapOf(
                        "summary" to "Summary",
                        "issuetype" to mapOf("id" to "10101"),
                        "customfield_10102" to 8.0,
                        "customfield_13017" to mapOf("value" to "L")
                )))))

        val issues = jiraService.loadRecentIssues("testproject")
        assertThat(issues).hasSize(1)
        assertThat(issues[0].weight).isEqualTo("L")
        assertThat(issues[0].key).isEqualTo("STORY-1")
        assertThat(issues[0].content).isEqualTo("Summary")
        assertThat(issues[0].template).isEqualTo(PrintTemplate.STORY)
    }

    @Test
    fun `correct project mapping`() {
        every { jiraRestTemplate.getForEntity(any<String>(), Array<JiraProject>::class.java) } returns ResponseEntity.ok(arrayOf(
                JiraProject(
                        key = "PROJ",
                        name = "Testproject",
                        avatarUrls = AvatarUrls("48x48", "32x32", "24x24", "16x16")
                )))

        every { jiraRestTemplate.getForEntity(any<String>(), ByteArray::class.java) } returns ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/png").body("".toByteArray())

        val projects = jiraService.projects!!
        assertThat(projects).hasSize(1)
        assertThat(projects[0].avatar).isEqualToComparingFieldByField(PossyAvatar("png", "".toByteArray()))
        assertThat(projects[0].key).isEqualTo("PROJ")
        assertThat(projects[0].name).isEqualTo("Testproject")
    }
}
