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

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import net.g24.possy.service.model.PossyIssue
import net.g24.possy.service.model.PrintTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/print")
@Api("Print Queue", tags = ["Print Queue"], description = "Pick up, create and delete print requests")
class PrintApiService(@Autowired private val queue: PrintRequestQueueService) {

    @GetMapping
    @ApiOperation("Get all print requests from print queue")
    fun nextRequest(): ResponseEntity<List<PossyIssue>> = ResponseEntity.ok(queue.nextAllItems().toList())

    @PostMapping
    @ApiOperation("Adds a new print request to the print queue")
    fun createRequest(
            @RequestParam("template") @ApiParam("The content template") template: PrintTemplate,
            @RequestParam("issue") @ApiParam("Usually the key/identifier (e.g. JIRA-123) of an issue") issue: String,
            @RequestParam("content") @ApiParam("Plain text content") content: String): ResponseEntity<PossyIssue> = try {
        ResponseEntity.ok(queue.addItem(PossyIssue(template, issue, null, null, content)))
    } catch (e: IllegalArgumentException) {
        ResponseEntity.badRequest().build()
    }

    @DeleteMapping("{id}")
    @ApiOperation("Removes a specific print request from print queue")
    fun removeRequest(
            @PathVariable("id")
            @ApiParam("The ID/identifier of the print request to remove")
            id: UUID
    ): ResponseEntity<Any> = if (queue.removeItem(id)) ResponseEntity.ok().build() else ResponseEntity.notFound().build()
}
