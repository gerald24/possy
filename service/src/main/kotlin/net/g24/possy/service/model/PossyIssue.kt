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
package net.g24.possy.service.model

import java.util.*

class PossyIssue(
        val id: UUID = UUID.randomUUID(),
        val template: PrintTemplate,
        val key: String?,
        val weight: String?,
        val tag: String?,
        val content: String
) {
    var consumed: Boolean = false

    val isValid: Boolean
        get() = (template === PrintTemplate.FREEFORM || !key.isNullOrBlank()) && content.isNotEmpty()

    override fun toString(): String {
        return key ?: ""
    }

    fun contentEquals(other: PossyIssue): Boolean {
        return template == other.template
                && template != PrintTemplate.FREEFORM
                && key == other.key
                && content.contentEquals(other.content)
    }
}
