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

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.util.*

@ApiModel(description = "Model for print requests")
class PossyIssue(
        @ApiModelProperty(notes = "Template for layout")
        val template: PrintTemplate,

        @ApiModelProperty("Usually the key/identifier (e.g. JIRA-123) of an issue")
        val key: String?,

        @ApiModelProperty("E.g. story points")
        val weight: String?,

        @ApiModelProperty("E.g. author or other meta info")
        val tag: String?,

        private val mimetype: String,

        @ApiModelProperty("E.g. text, images etc.")
        val content: ByteArray
) {

    @ApiModelProperty(notes = "Unique ID for a print request")
    val id = UUID.randomUUID()

    @ApiModelProperty("Is the print request already consumed by any client?")
    var consumed: Boolean = false

    val isValid: Boolean
        @ApiModelProperty(hidden = true)
        get() = (template === PrintTemplate.FREEFORM || !key.isNullOrBlank()) && content.isNotEmpty()

    val contentAsString: String
        @ApiModelProperty(hidden = true)
        get() = if (template === PrintTemplate.IMAGE) "" else String(content)

    constructor(
            template: PrintTemplate, issue: String?, weight: String?, tag: String?, content: String) : this(template, issue, weight, tag, "text/plain", content.toByteArray()) {
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
