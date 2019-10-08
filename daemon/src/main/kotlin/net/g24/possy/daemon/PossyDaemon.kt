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

import net.g24.possy.daemon.configuration.PossyProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class PossyDaemon(possyProperties: PossyProperties, private val possyService: PossyService, restTemplateBuilder: RestTemplateBuilder) {

    companion object {
        private const val GET_URL = "/api/print"
        private const val DELETE_URL = "/api/print/%s"
    }

    private val restTemplate: RestTemplate = restTemplateBuilder
            .rootUri(possyProperties.service.url)
            .basicAuthentication(possyProperties.service.username, possyProperties.service.password)
            .build()

    @Scheduled(fixedDelay = 10000)
    fun checkForPrint() {
        logger().trace("scheduled check")

        val printRequests = restTemplate.getForObject(GET_URL, Array<PrintRequest>::class.java)
        if (printRequests.isNullOrEmpty()) {
            return
        }

        printRequests.forEach { printRequest ->
            logger().info("processing {}", printRequest)
            possyService.print(printRequest)
            restTemplate.delete(DELETE_URL.format(printRequest.id.toString()))
        }
    }
}
