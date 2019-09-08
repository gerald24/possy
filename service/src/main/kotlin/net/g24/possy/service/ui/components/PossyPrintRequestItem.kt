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
package net.g24.possy.service.ui.components

import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.polymertemplate.PolymerTemplate
import com.vaadin.flow.templatemodel.TemplateModel
import net.g24.possy.service.model.PossyIssue

@Tag("possy-print-request-item")
@JsModule("./src/possy-print-request-item.js")
class PossyPrintRequestItem(printRequest: PossyIssue) : PolymerTemplate<PossyPrintRequestItem.PossyPrintRequestItemModel>() {

    interface PossyPrintRequestItemModel : TemplateModel {
        fun setHeader(header: String)
        fun setContent(content: String)
        fun setTemplate(template: String)
        fun setTemplateName(templateName: String)
        fun setStatus(status: String)
    }

    init {
        setId(printRequest.id.toString())
        model.setHeader(printRequest.key ?: "")
        model.setContent(printRequest.contentAsString)
        model.setTemplate(printRequest.template.toString())
        model.setTemplateName(printRequest.template.name)
        setConsumed(printRequest.isConsumed)
    }

    fun setConsumed(consumed: Boolean) {
        model.setStatus(if (consumed) "Printing..." else "Queued")
    }

}
