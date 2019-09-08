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
package net.g24.possy.service.api;

import net.g24.possy.service.model.PrintRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

// TODO (https://github.com/gerald24/possy/issues/5) convert to kotlin

/**
 * @author: Gerald Leeb
 */
@Service
public class PrintRequestQueueService {

    private final List<Listener> listeners = new ArrayList<>();
    private final Queue<PrintRequest> queue = new ConcurrentLinkedQueue<>();

    // TODO replace with eventbus ?
    public interface Listener {

        void itemAdded(PrintRequest request);

        void itemConsumed(PrintRequest request);

        void itemRemoved(PrintRequest request);
    }

    public Runnable addListener(Listener listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    public PrintRequest addItem(PrintRequest item) {
        if (item == null || !item.isValid()) {
            throw new RuntimeException("invalid item");
        }
        Optional<PrintRequest> contains = queue.stream().filter(item::contentEquals).findFirst();
        if (contains.isPresent()) {
            return contains.get();
        }

        queue.add(item);
        listeners.forEach(listener -> listener.itemAdded(item));
        return item;
    }

    public Collection<PrintRequest> nextAllItems() {
        Collection<PrintRequest> items = allItems();
        items.forEach(item -> {
            item.markAsConsumed();
            listeners.forEach(listener -> listener.itemConsumed(item));
        });
        return items;
    }

    public boolean removeItem(UUID id) {
        Optional<PrintRequest> itemOptional = queue.stream().filter(r -> r.getId().equals(id)).findFirst();
        if (itemOptional.isPresent()) {
            PrintRequest item = itemOptional.get();
            boolean result = queue.remove(item);
            listeners.forEach(listener -> listener.itemRemoved(item));
            return result;
        }
        return false;
    }

    public Collection<PrintRequest> allItems() {
        return Collections.unmodifiableCollection(queue);
    }

}
