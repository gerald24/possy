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

package net.g24.possy.service.api

import net.g24.possy.service.model.PrintRequest
import net.g24.possy.service.model.PrintTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * @author Gerald Leeb
 * @author Alex Gassner
 */
@RestController
@RequestMapping("/api/print")
class ApiController(@Autowired private val queue: PrintRequestQueueService) {

    @GetMapping
    fun nextRequest(): ResponseEntity<List<PrintRequest>> = ResponseEntity.ok(queue.nextAllItems().toList())

    @PostMapping
    fun createRequest(
            @RequestParam("template") template: String,
            @RequestParam("issue") issue: String,
            @RequestParam("content") content: String): ResponseEntity<PrintRequest> =
            ResponseEntity.ok(queue.addItem(PrintRequest(PrintTemplate.forValue(template), issue, null, null, content)))

    @DeleteMapping("{id}")
    fun removeRequest(@PathVariable("id") id: String): ResponseEntity<Any> =
            if (queue.removeItem(UUID.fromString(id))) ResponseEntity.ok().build() else ResponseEntity.notFound().build()
}
