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

import io.swagger.annotations.*
import net.g24.possy.service.model.PrintPaper
import net.g24.possy.service.rendering.PdfGenerator
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/print")
@Api("Print Queue", tags = ["Print Queue"], description = "Pick up and delete print requests")
class PrintApiService(private val queue: PrintRequestQueueService, private val pdfGenerator: PdfGenerator) {

    @GetMapping
    @ApiOperation("Get all print requests from print queue")
    fun nextRequests(): ResponseEntity<List<PrintRequest>> =
            ResponseEntity.ok(queue.nextAllItems().map { PrintRequest(
                    id = it.id,
                    printPaper = it.template.paper,
                    content = pdfGenerator.createPdf(it),
                    mimeType = MediaType.APPLICATION_PDF_VALUE
            )})

    @DeleteMapping("{id}")
    @ApiOperation("Removes a specific print request from print queue")
    fun removeRequest(
            @PathVariable("id")
            @ApiParam("The ID/identifier of the print request to remove")
            id: UUID
    ): ResponseEntity<Any> = if (queue.removeItem(id)) ResponseEntity.ok().build() else ResponseEntity.notFound().build()
}

@ApiModel(description = "Model for print requests")
class PrintRequest(

        @ApiModelProperty(notes = "Identifier of the print request")
        val id: UUID,

        @ApiModelProperty(notes = "Paper type for choosing the correct printer")
        val printPaper: PrintPaper,

        @ApiModelProperty("The content to print, e.g. text, images etc.")
        val content: ByteArray,

        @ApiModelProperty("The content's type")
        val mimeType: String
)
