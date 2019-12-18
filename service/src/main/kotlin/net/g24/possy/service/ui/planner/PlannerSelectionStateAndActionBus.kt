package net.g24.possy.service.ui.planner

import com.vaadin.flow.spring.annotation.UIScope
import org.springframework.stereotype.Component

@Component
@UIScope
class PlannerSelectionStateAndActionBus() {

    var selectedTask: TaskPlannerView? = null
    var selectedStory: StoryPlannerView? = null

    private var selectionChangedHandlers = mutableListOf<() -> Unit>()

    var addStoryClickHandler: () -> Unit = {}
    var removeStoryClickHandler: () -> Unit = {}
    var addTaskClickHandler: () -> Unit = {}
    var removeTaskClickHandler: () -> Unit = {}
    var printClickHandler: () -> Unit = {}
    var resetClickHandler: () -> Unit = {}

    fun addSelectionChangedHandler(handler: () -> Unit) {
        selectionChangedHandlers.add(handler)
    }

    fun addStoryClicked() {
        addStoryClickHandler()
    }

    fun addTaskClicked() {
        addTaskClickHandler()
    }

    fun removeTaskClicked() {
        removeTaskClickHandler()
    }

    fun removeStoryClicked() {
        removeStoryClickHandler()
    }

    fun printClicked() {
        printClickHandler()
    }

    fun resetClicked() {
        resetClickHandler()
    }

    fun selectStory(storyView: StoryPlannerView?, taskView: TaskPlannerView?) {
        selectedStory = storyView
        selectedTask = taskView
        selectionChangedHandlers.forEach { it() }
    }

    fun selectTask(taskView: TaskPlannerView) {
        selectedTask = taskView
        selectionChangedHandlers.forEach { it() }
    }


}
