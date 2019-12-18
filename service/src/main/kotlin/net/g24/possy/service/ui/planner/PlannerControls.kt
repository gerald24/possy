package net.g24.possy.service.ui.planner

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.spring.annotation.UIScope
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
@UIScope
class PlannerControls(private val plannerSelectionEventAndActionBus: PlannerSelectionStateAndActionBus) : VerticalLayout() {

    private val contentField = TextArea().apply {
        label = "Task"
        width = "200px"
        height = "240px"
        value = "content" // TODO
        valueChangeMode = ValueChangeMode.EAGER
        addValueChangeListener { field ->
            plannerSelectionEventAndActionBus.selectedTask?.let { it.setContent(field.value) }
        }
    }
    private val addTaskButton = Button("Add New Task", VaadinIcon.FILE_ADD.create()) { plannerSelectionEventAndActionBus.addTaskClicked() }
    private val removeTaskButton = Button("Delete Task", VaadinIcon.FILE_REMOVE.create()) { plannerSelectionEventAndActionBus.removeTaskClicked() }
    private val addStoryButton = Button("Add Story", VaadinIcon.FOLDER_ADD.create()) { plannerSelectionEventAndActionBus.addStoryClicked() }
    private val removeStoryButton = Button("Delete Story", VaadinIcon.FOLDER_REMOVE.create()) { plannerSelectionEventAndActionBus.removeStoryClicked() }
    private val printButton = Button("Print", VaadinIcon.PRINT.create()) { plannerSelectionEventAndActionBus.printClicked() }
    private val resetButton = Button("Reset", VaadinIcon.CLOSE.create()) { plannerSelectionEventAndActionBus.resetClicked() }

    @PostConstruct
    fun init() {
        element.style.set("background", "#d0d0d0")
        width = "240px"
        setHeightFull()
        add(contentField, Div(addTaskButton, removeTaskButton, addStoryButton, removeStoryButton, printButton, resetButton))

        plannerSelectionEventAndActionBus.addSelectionChangedHandler { refreshAppearance() }
    }

    private fun refreshAppearance() {
        contentField.value = plannerSelectionEventAndActionBus.selectedTask?.possyIssue?.content ?: ""
        addTaskButton.isEnabled = plannerSelectionEventAndActionBus.selectedStory != null
        removeTaskButton.isEnabled = plannerSelectionEventAndActionBus.selectedTask != null
        removeStoryButton.isEnabled = plannerSelectionEventAndActionBus.selectedStory != null

        printButton.isEnabled = plannerSelectionEventAndActionBus.selectedStory != null
        resetButton.isEnabled = plannerSelectionEventAndActionBus.selectedStory != null
    }
}
