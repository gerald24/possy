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
import org.springframework.stereotype.Service

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @author: Gerald Leeb
 */
@Service
class PrintRequestQueueService {

    private val listeners = ArrayList<Listener>()
    private val queue = ConcurrentLinkedQueue<PossyIssue>()

    interface Listener {

        fun itemAdded(request: PossyIssue)

        fun itemConsumed(request: PossyIssue)

        fun itemRemoved(request: PossyIssue)
    }

    fun addListener(listener: Listener): Runnable {
        listeners.add(listener)
        return Runnable() { listeners.remove(listener) }
    }

    fun addItem(item: PossyIssue?): PossyIssue {
        if (item == null || !item.isValid) {
            throw RuntimeException("invalid item")
        }
        queue.firstOrNull { item.contentEquals(it) }?.let { return it }

        queue.add(item)
        listeners.forEach { listener -> listener.itemAdded(item) }
        return item
    }

    fun nextAllItems(): Collection<PossyIssue> {
        val items = allItems()
        items.forEach { item ->
            item.markAsConsumed()
            listeners.forEach { listener -> listener.itemConsumed(item) }
        }
        return items
    }

    fun removeItem(id: UUID): Boolean {
        val itemOptional = queue.stream().filter { r -> r.id == id }.findFirst()
        if (itemOptional.isPresent) {
            val item = itemOptional.get()
            val result = queue.remove(item)
            listeners.forEach { listener -> listener.itemRemoved(item) }
            return result
        }
        return false
    }

    fun allItems(): Collection<PossyIssue> {
        return Collections.unmodifiableCollection(queue)
    }

}
