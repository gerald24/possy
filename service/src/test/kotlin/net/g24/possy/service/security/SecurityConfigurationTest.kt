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

package net.g24.possy.service.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.util.*
import javax.annotation.PostConstruct


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SecurityConfigurationTest {

    @LocalServerPort
    private var localServerPort: Int = 0

    @Autowired
    private lateinit var securityProperties: SecurityProperties

    private lateinit var baseUrl: String

    @PostConstruct
    private fun init() {
        baseUrl = "http://localhost:$localServerPort"
    }

    @Test
    fun `api not public`() {
        try {
            RestTemplate().getForEntity("$baseUrl/api/print", String::class.java)
            fail("should not succeed")
        } catch (e: HttpClientErrorException) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.statusCode)
        }
    }

    @Test
    fun `api secured with basic auth app user`() {
        val request = HttpEntity<String>(basicAuthHttpHeader(securityProperties.user.name, securityProperties.user.password));
        val response = RestTemplate().exchange("$baseUrl/api/print", HttpMethod.GET, request, String::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `redirect to login page when unauthenticated`() {
        val response = RestTemplate().getForEntity("$baseUrl/projects", String::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("$baseUrl/login", response.headers.location!!.toString())
    }

    @Test
    fun `successful login`() {
        login(securityProperties.user.name, securityProperties.user.password)
    }

    @Test
    fun `unsuccessful login`() {
        login("h4xX0rZ", "1337", false)
    }

    @Test
    fun `actuator public endpoints`() {
        arrayOf("health", "info").forEach {
            val response = RestTemplate().getForEntity("$baseUrl/actuator/$it", String::class.java)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }

    private fun basicAuthHttpHeader(username: String, password: String): HttpHeaders {
        val credentials = Base64.getEncoder().encodeToString("$username:$password".toByteArray())
        val headers = HttpHeaders()
        headers.add("Authorization", "Basic $credentials")
        return headers
    }

    private fun login(username: String, password: String, assertSuccess: Boolean = true) {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val map = LinkedMultiValueMap<String, String>()
        map.add("username", username)
        map.add("password", password)
        map.add("remember-me", "true")

        val request = HttpEntity<MultiValueMap<String, String>>(map, headers)
        val response = RestTemplate().postForEntity("$baseUrl/login", request, String::class.java)
        val location = response.headers.location!!.toString()
        var sessionId = ""
        var rememberMeCookie = false

        response.headers[HttpHeaders.SET_COOKIE]?.forEach {
            val cookie = it.split(";")[0].split("=")
            if (cookie[0] == "JSESSIONID") {
                sessionId = cookie[1]
            }
            if (cookie[0] == "remember-me") {
                rememberMeCookie = true
            }
        }

        assertThat(rememberMeCookie).isTrue()
        assertThat(response.statusCode).isEqualTo(HttpStatus.FOUND)

        if (assertSuccess) {
            assertThat(location).isEqualTo("$baseUrl/")
            assertThat(sessionId).isNotBlank()

            val headers1 = HttpHeaders()
            headers.add("Cookie", "JSESSIONID=$sessionId")
            val response1 = RestTemplate().exchange(location, HttpMethod.GET, HttpEntity(null, headers1), String::class.java)
            assertThat(response1.statusCode).isEqualTo(HttpStatus.OK)
            return
        }

        assertThat(location).isEqualTo("$baseUrl/login?error")
    }
}
