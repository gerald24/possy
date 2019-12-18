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

import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import net.g24.possy.service.service.PrintRequestQueueService
import net.g24.possy.service.ui.MainLayout
import net.g24.possy.service.ui.PageTitleBuilder
import org.springframework.beans.factory.ObjectProvider
import javax.annotation.PostConstruct

@Route("planner", layout = MainLayout::class)
class PlannerView(
        private val plannerSelectionEventAndActionBus: PlannerSelectionStateAndActionBus,
        private val printRequestQueueService: PrintRequestQueueService,
        private val pageTitleBuilder: PageTitleBuilder,
        private val controlPanel: PlannerControls,
        private val storyViewProvider: ObjectProvider<StoryPlannerView>

) : HorizontalLayout(), HasDynamicTitle {

    // TODO add persistence!

    // TODO add drag/drop rearrange support for new components

    // TODO remove old PossyPlanner
    // private val possyPlanner = PossyPlanner { addPrintRequests(it) }

    private val storiesContainer = Div().apply { addClassName("planner-stories") }

    @PostConstruct
    private fun init() {
        setSizeFull()

        addClassName("planner-view")
        add(controlPanel, storiesContainer)

        plannerSelectionEventAndActionBus.addStoryClickHandler = this::addStory
    }


    override fun getPageTitle(): String = pageTitleBuilder.build("Planner")

    private fun addStory() {
        StoryKeyDialog {
            var storyView = storyViewProvider.getObject()
            storyView.key = it
            storiesContainer.add(storyView)
            plannerSelectionEventAndActionBus.selectStory(storyView, storyView.addTask())
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
        plannerSelectionEventAndActionBus.selectStory(null, null)
    }


}
