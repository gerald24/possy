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


import net.g24.possy.service.model.PossyIssue
import net.g24.possy.service.model.PrintTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * @author: Gerald Leeb
 */
@RestController
class ApiService @Autowired
constructor(private val queue: PrintRequestQueueService) {

    @RequestMapping(value = ["/printitem/next"], method = [RequestMethod.GET])
    fun nextRequest(): Collection<PossyIssue> {
        return queue.nextAllItems()
    }

    @RequestMapping(value = ["/printitem/create"], method = [RequestMethod.POST])
    fun createRequest(
            @RequestParam(name = "template") template: String,
            @RequestParam(name = "issue") issue: String,
            @RequestParam(name = "content") content: String): PossyIssue {
        return queue.addItem(PossyIssue(PrintTemplate.forValue(template), issue, null, null, content))
    }

    @RequestMapping(value = ["/printitem/{id}"], method = [RequestMethod.DELETE])
    fun removeRequest(@PathVariable("id") id: String): ResponseEntity<*> {
        return if (queue.removeItem(UUID.fromString(id))) {
            ResponseEntity<Any>(HttpStatus.NO_CONTENT)
        } else ResponseEntity<Any>(HttpStatus.NOT_FOUND)
    }
}
