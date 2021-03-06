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

import com.vaadin.flow.component.ClientCallable
import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.polymertemplate.PolymerTemplate
import com.vaadin.flow.templatemodel.TemplateModel
import elemental.json.JsonArray
import net.g24.possy.service.model.PossyIssue
import net.g24.possy.service.model.PrintTemplate

@Tag("possy-planner")
@JsModule("./src/planner/possy-planner.js")
class PossyPlanner(private val clickHandler: (printRequests: List<PossyIssue>) -> Unit) : PolymerTemplate<TemplateModel>() {

    @ClientCallable
    private fun print(json: JsonArray) {
        val printRequests = mutableListOf<PossyIssue>()
        for (i in 1..json.length()) {
            val story = json.getObject(i - 1)
            var storyDetail = story.getString("detail")
            if (storyDetail.isNullOrBlank()) {
                storyDetail = "Story"
            }
            val tasks = story.getArray("tasks")
            var headerPrinted = false

            for (j in 1..tasks.length()) {
                val task = tasks.getObject(j - 1)
                val taskDetail = task.getString("detail")

                if (!taskDetail.isNullOrBlank()) {
                    if (!headerPrinted) {
                        printRequests.add(
                                PossyIssue(
                                        PrintTemplate.FREEFORM,
                                        storyDetail,
                                        null, null, "*******************\n*******************\n\nS E P A R A T O R\n\n*******************\n*******************"
                                )
                        )
                        headerPrinted = true
                    }
                    printRequests.add(PossyIssue(PrintTemplate.FREEFORM, null, null, storyDetail, taskDetail))
                }
            }
        }

        clickHandler.invoke(printRequests.toList())
    }
}
