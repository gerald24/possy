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

package net.g24.possy.service.extensions

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.util.*
import kotlin.reflect.KProperty

// solution found here: https://stackoverflow.com/questions/35752575/kotlin-lazy-properties-and-values-reset-a-resettable-lazy-delegate
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
class ResettableLazyManager {
    // we synchronize to make sure the timing of a reset() call and new inits do not collide
    val managedDelegates = LinkedList<Resettable>()

    fun register(managed: Resettable) {
        synchronized (managedDelegates) {
            managedDelegates.add(managed)
        }
    }

    fun reset() {
        synchronized (managedDelegates) {
            managedDelegates.forEach { it.reset() }
            managedDelegates.clear()
        }
    }
}

interface Resettable {
    fun reset()
}

class ResettableLazy<PROPTYPE>(val manager: ResettableLazyManager, val init: ()->PROPTYPE): Resettable {
    @Volatile var lazyHolder = makeInitBlock()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): PROPTYPE {
        return lazyHolder.value
    }

    override fun reset() {
        lazyHolder = makeInitBlock()
    }

    fun makeInitBlock(): Lazy<PROPTYPE> {
        return lazy {
            manager.register(this)
            init()
        }
    }
}

fun <PROPTYPE> resettableLazy(manager: ResettableLazyManager, init: ()->PROPTYPE): ResettableLazy<PROPTYPE> {
    return ResettableLazy(manager, init)
}
