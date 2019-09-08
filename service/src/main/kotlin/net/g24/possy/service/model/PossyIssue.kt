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


/**
 * @author: Gerald Leeb
 */
class PossyIssue(val template: PrintTemplate, val key: String?, val weight: String?, val tag: String?, private val mimetype: String, val content: ByteArray) {
    val id = UUID.randomUUID()
    var isConsumed: Boolean = false
        private set

    val isValid: Boolean
        get() = (template === PrintTemplate.FREEFORM || !key.isNullOrBlank()) && content.isNotEmpty()

    val contentAsString: String
        get() = if (template === PrintTemplate.IMAGE) "" else String(content)

    constructor(
            template: PrintTemplate, issue: String?, weight: String?, tag: String?, content: String) : this(template, issue, weight, tag, "text/plain", content.toByteArray()) {
    }

    fun markAsConsumed() {
        isConsumed = true
    }

    override fun toString(): String {
        return key ?: ""
    }

    fun contentEquals(other: PossyIssue): Boolean {
        return this.template == other.template &&
                this.template !== PrintTemplate.FREEFORM && this.key == other.key &&
                this.mimetype == other.mimetype &&
                this.content.contentEquals(other.content)
    }
}
