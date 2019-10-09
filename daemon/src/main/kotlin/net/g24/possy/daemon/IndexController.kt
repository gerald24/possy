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

import org.springframework.boot.info.GitProperties
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RestController
class IndexController(private val gitProperties: GitProperties) {

    @GetMapping("/")
    fun index(): ResponseEntity<String> {
        return ResponseEntity.ok("Hi! I'm possy daemon " + versionInfo())
    }

    private fun versionInfo(): String {
        val combinedVersion = gitProperties.get("commit.id.describe")
        val buildTime = gitProperties.getInstant("build.time")
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime()
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        return "$combinedVersion, $buildTime"
    }
}
