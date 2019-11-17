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

package net.g24.possy.daemon

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.GitProperties
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

@WebMvcTest(IndexController::class)
@ExtendWith(SpringExtension::class)
class IndexControllerTest {

    @MockkBean
    private lateinit var gitProperties: GitProperties

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `index shows version info`() {
        every { gitProperties.get("commit.id.describe") } returns "1.0.0"
        every { gitProperties.getInstant("build.time") } returns Instant.now()

        mockMvc.perform(get("/"))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString(gitProperties.get("commit.id.describe"))))
    }
}
