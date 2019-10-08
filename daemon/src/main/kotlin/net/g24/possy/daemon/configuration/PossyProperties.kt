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
@ConfigurationProperties(prefix = "possy")
@Validated
class PossyProperties(
        val service: Service = Service(),
        val pdfGenerator: PdfGenerator = PdfGenerator()) {

    class Service(
            var url: String? = null,
            var username: String? = null,
            var password: String? = null)

    class PdfGenerator(
            var isRedirectToFile: Boolean = false,
            val fonts: Fonts = Fonts(),
            val pages: Pages = Pages()) {

        class Fonts(
                val header: FontSpec = FontSpec(),
                val content: FontSpec = FontSpec(),
                val footer: FontSpec = FontSpec()) {

            class FontSpec(
                    var font: String? = null,
                    var size: Int = 0,
                    var lineHeight: Int = 0)
        }

        class Pages(
                val story: PageSpec = PageSpec(),
                val task: PageSpec = PageSpec(),
                val freeform: PageSpec = PageSpec(),
                val bug: PageSpec = PageSpec()) {

            class PageSpec(var width: Float = 0.toFloat(),
                           var height: Float = 0.toFloat(),
                           var border: Float = 0.toFloat(),
                           var margin: Float = 0.toFloat())
        }
    }
}
