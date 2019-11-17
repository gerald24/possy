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

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest
class PdfGeneratorTest {

    @MockBean
    private lateinit var possyService: PossyService

    @MockBean
    private lateinit var possyDaemon: PossyDaemon

    @Autowired
    private lateinit var pdfGenerator: PdfGenerator

    @Test
    fun `special chars are printed without throwing an exception`() {
        pdfGenerator.createPdf(PrintRequest(
                id = UUID.randomUUID(),
                template = PrintTemplate.BUG,
                weight = "XL",
                key = "KEY-1",
                tag = "TAG-1",
                content = "\u00AD".toByteArray(), // soft hypen
                mimetype = "plain/text"))
    }
}
