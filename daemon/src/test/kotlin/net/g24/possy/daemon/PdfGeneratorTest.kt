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

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.random.Random

@SpringBootTest
@ExtendWith(SpringExtension::class)
class PdfGeneratorTest {

    @MockBean
    private lateinit var possyService: PossyService

    @MockBean
    private lateinit var possyDaemon: PossyDaemon

    @Autowired
    private lateinit var pdfGenerator: PdfGenerator

    @Test
    fun `special chars are printed without throwing an exception`() {
        PrintTemplate.values().forEach { printTemplate ->
            pdfGenerator.createPdf(PrintRequest(
                    id = UUID.randomUUID(),
                    template = printTemplate,
                    weight = "XL",
                    key = "KEY-1",
                    tag = "TAG-1",
                    content = "${randomContent()}\u00AD".toByteArray(), // soft hypen
                    mimetype = "plain/text"))
        }
    }

    private fun randomContent() : String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz"
        return (1..Random.nextInt(10, 15)) // words
                .map { (1..Random.nextInt(5, 15)) // word
                        .map { allowedChars.random() }
                        .joinToString("") }
                .joinToString(" ")
    }
}
