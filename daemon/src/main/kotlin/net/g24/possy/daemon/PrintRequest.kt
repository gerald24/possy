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

import java.nio.charset.StandardCharsets
import java.util.*

class PrintRequest(
        val id: UUID,
        val template: PrintTemplate,
        val key: String? = null,
        val weight: String? = null,
        val tag: String? = null,
        val content: ByteArray,
        val mimetype: String? = null) {

    val contentAsString: String
        get() = content.toString(StandardCharsets.UTF_8)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PrintRequest

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "PrintRequest(id=$id, template=$template, key=$key, weight=$weight, tag=$tag, mimetype=$mimetype)"
    }
}
