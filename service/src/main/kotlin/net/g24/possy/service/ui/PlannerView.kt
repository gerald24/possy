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
package net.g24.possy.service.ui

import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import net.g24.possy.service.model.PossyIssue
import net.g24.possy.service.service.PrintRequestQueueService
import net.g24.possy.service.ui.components.PossyPlanner

@Route("planner", layout = MainLayout::class)
class PlannerView(private val printRequestQueueService: PrintRequestQueueService, private val pageTitleBuilder: PageTitleBuilder)
    : VerticalLayout(), HasDynamicTitle {

    private val possyPlanner = PossyPlanner { addPrintRequests(it) }

    init {
        addClassName("possy-jql")
        add(possyPlanner)
    }

    override fun getPageTitle(): String = pageTitleBuilder.build("Planner")

    private fun addPrintRequests(requests: List<PossyIssue>) {
        requests.forEach { printRequestQueueService.addItem(it) }
        Notification.show("Added to print queued", 600, Notification.Position.MIDDLE)
    }

}
