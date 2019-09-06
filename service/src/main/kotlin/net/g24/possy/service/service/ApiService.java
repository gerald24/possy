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
package net.g24.possy.service.service;

import java.util.Collection;
import java.util.UUID;

import net.g24.possy.service.model.PrintRequest;
import net.g24.possy.service.model.PrintTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO (https://github.com/gerald24/possy/issues/5) convert to kotlin

/**
 * @author: Gerald Leeb
 */
@RestController
public class ApiService {

    private final PrintRequestQueueService queue;

    @Autowired
    public ApiService(PrintRequestQueueService queue) {
        this.queue = queue;
    }

    @RequestMapping(value = "/printitem/next", method = RequestMethod.GET)
    public Collection<PrintRequest> nextRequest() {
        return queue.nextAllItems();
    }

    @RequestMapping(value = "/printitem/create", method = RequestMethod.POST)
    public PrintRequest createRequest(
            @RequestParam(name = "template") String template,
            @RequestParam(name = "issue") String issue,
            @RequestParam(name = "content") String content) {
        return queue.addItem(new PrintRequest(PrintTemplate.forValue(template), issue, null, null, content));
    }

    @RequestMapping(value = "/printitem/{id}", method = RequestMethod.DELETE)
    public ResponseEntity removeRequest(@PathVariable("id") String id) {
        if (queue.removeItem(UUID.fromString(id))) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
