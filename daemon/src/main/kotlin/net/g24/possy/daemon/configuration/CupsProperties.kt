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
package net.g24.possy.daemon.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Configuration
@ConfigurationProperties(prefix = "cups")
@Validated
class CupsProperties(
        var host: String? = null,
        var port: Int = 0,
        val printers: Printers = Printers()) {

    class Printers(
            var white: String? = null,
            var pink: String? = null,
            var yellow: String? = null)
}
