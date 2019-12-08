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
package net.g24.possy.service.ui.planner

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import net.g24.possy.service.service.PrintRequestQueueService
import net.g24.possy.service.ui.MainLayout
import net.g24.possy.service.ui.PageTitleBuilder
import org.springframework.beans.factory.ObjectProvider

@Route("planner", layout = MainLayout::class)
class PlannerView(
        private val printRequestQueueService: PrintRequestQueueService,
        private val pageTitleBuilder: PageTitleBuilder,
        private val storyViewProvider: ObjectProvider<StoryPlannerView>
) : VerticalLayout(), HasDynamicTitle {

    // TODO add persistence!

    // TODO add drag/drop rearrange support for new components

    // TODO remove old PossyPlanner
    // private val possyPlanner = PossyPlanner { addPrintRequests(it) }
    private val storiesContainer = Div().apply { addClassName("planner-stories") }
    private val addButton = Button("Add Story", VaadinIcon.FOLDER_ADD.create()) { addStory() }
    private val printButton = Button("Print", VaadinIcon.PRINT.create()) { printStories() }
    private val resetButton = Button("Reset", VaadinIcon.CLOSE.create()) { reset() }

    init {
        addClassName("planner-view")
        add(storiesContainer, HorizontalLayout(addButton, printButton, resetButton))
        refreshAppearance()
    }

    override fun getPageTitle(): String = pageTitleBuilder.build("Planner")


    private fun addStory() {
        StoryKeyDialog {
            val storyPlannerView = storyViewProvider.getObject()
            storyPlannerView.key = it
            storyPlannerView.addTask()
            storyPlannerView.removeHandler = {
                storiesContainer.remove(storyPlannerView)
                refreshAppearance()
            }

            storiesContainer.add(storyPlannerView)
            refreshAppearance()
        }.open()
    }

    private fun printStories() {
        // TODO add confirmation
        // TODO print
//         requests.forEach { printRequestQueueService.addItem(it) }
        Notification.show("Added to print queued", 600, Notification.Position.MIDDLE)
    }


    private fun reset() {
        // TODO add confirmation
        storiesContainer.element.removeAllChildren()
        refreshAppearance()
    }

    private fun refreshAppearance() {
        val hasChildren = storiesContainer.element.childCount > 0
        printButton.isEnabled = hasChildren;
        resetButton.isEnabled = hasChildren;
    }
}
