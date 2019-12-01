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

package net.g24.possy.service.service

import net.g24.possy.service.model.PossyIssue
import net.g24.possy.service.model.PrintTemplate
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith(SpringExtension::class)
class PrintApiServiceTest {

    @Autowired
    private lateinit var printRequestQueueService: PrintRequestQueueService

    @Autowired
    private lateinit var securityProperties: SecurityProperties

    @Autowired
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        printRequestQueueService.clear()
    }

    @Test
    fun `next request`() {
        mockMvc.perform(get("/api/print")
                .with(httpBasic(securityProperties.user.name, securityProperties.user.password)))
                .andExpect(status().isOk)
                .andExpect(content().string("[]"))

        val id = addQueueItem()

        mockMvc.perform(get("/api/print")
                .with(httpBasic(securityProperties.user.name, securityProperties.user.password)))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString(id.toString())))
    }

    @Test
    fun `invalid credentials`() {
        mockMvc.perform(get("/api/print")).andExpect(status().isUnauthorized)
        mockMvc.perform(post("/api/print")).andExpect(status().isUnauthorized)
        mockMvc.perform(delete("/api/print")).andExpect(status().isUnauthorized)
    }

    @Test
    fun `valid remove request`() {
        val issueId = addQueueItem()
        mockMvc.perform(delete("/api/print/${issueId}")
                .with(httpBasic(securityProperties.user.name, securityProperties.user.password)))
                .andExpect(status().isOk)
    }

    @Test
    fun `invalid remove request`() {
        mockMvc.perform(delete("/api/print/not-existing-id")
                .with(httpBasic(securityProperties.user.name, securityProperties.user.password)))
                .andExpect(status().isBadRequest)

        mockMvc.perform(delete("/api/print/${UUID.randomUUID()}")
                .with(httpBasic(securityProperties.user.name, securityProperties.user.password)))
                .andExpect(status().isNotFound)
    }

    private fun addQueueItem() : UUID {
        val issue = printRequestQueueService.addItem(PossyIssue(
                template = PrintTemplate.BUG,
                key = "KEY-1",
                weight = "XL",
                content = "test",
                tag = "TAG-1"))
        return issue.id
    }
}
