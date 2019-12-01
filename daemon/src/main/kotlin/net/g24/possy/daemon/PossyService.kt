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

import net.g24.possy.daemon.configuration.CupsProperties
import net.g24.possy.daemon.configuration.PossyProperties
import org.cups4j.CupsClient
import org.cups4j.CupsPrinter
import org.cups4j.PrintJob
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.util.*

@Component
class PossyService(private val cupsProperties: CupsProperties, private val possyProperties: PossyProperties) {

    private var jobId = 0
    private val client: CupsClient = CupsClient(cupsProperties.host!!, cupsProperties.port)

    init {
        listAllPrinters(client)
    }

    fun print(printRequest: PrintRequest) {
        val attribs = HashMap<String, String>()
        attribs["document-format"] = printRequest.mimeType

        if (possyProperties.isRedirectToFile) {
            Files.write(Files.createTempFile("possy", ".pdf").toAbsolutePath(), printRequest.content)
            return
        }

        val cupsPrinter = findCupsPrinter(printRequest.printPaper)
        val pj = PrintJob.Builder(printRequest.content)
                .jobName("Possy #" + ++jobId)
                .attributes(attribs)
                .userName("anonymous")
                .copies(1)
                .build()
        cupsPrinter.print(pj)
    }

    private fun findCupsPrinter(printPaper: PrintPaper): CupsPrinter {
        val printer = when (printPaper) {
            PrintPaper.WHITE -> cupsProperties.printers.white!!
            PrintPaper.PINK -> cupsProperties.printers.pink!!
            PrintPaper.YELLOW -> cupsProperties.printers.yellow!!
        }

        return client.printers.first { printer == it.name }
    }

    private fun listAllPrinters(client: CupsClient) {
        client.printers.forEach { printer ->
            logger().info("Printer: {}", printer)

            logger().debug(" Media supported:")
            printer.mediaSupported.forEach { logger().debug("  - $it") }

            logger().debug(" Resolution supported:")
            printer.resolutionSupported.forEach { logger().debug("  - $it") }

            logger().debug(" Mime-Types supported:")
            printer.mimeTypesSupported.forEach { logger().debug("  - $it") }
        }
    }
}
