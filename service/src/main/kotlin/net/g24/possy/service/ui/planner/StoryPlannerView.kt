package net.g24.possy.service.ui.planner

import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import net.g24.possy.service.model.PossyIssue
import net.g24.possy.service.model.PrintTemplate
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class StoryPlannerView(
        private val plannerSelectionEventAndActionBus: PlannerSelectionStateAndActionBus,
        private val taskViewProvider: ObjectProvider<TaskPlannerView>
) : VerticalLayout() {

    private val titleComponent = H4()
    private val tasksContainer = Div().apply { addClassName("planner-tasks") }

    var key: String
        get() = titleComponent.text
        set(value) {
            titleComponent.text = value
        }

    var removeHandler: () -> Unit = {}

    init {
        addClassName("planner-story")
        add(titleComponent, tasksContainer)

        plannerSelectionEventAndActionBus.addTaskClickHandler = {
            plannerSelectionEventAndActionBus.selectTask(addTask())
        }
    }

    fun addTask(): TaskPlannerView {
        val taskPlannerView = taskViewProvider.getObject()
        taskPlannerView.possyIssue = PossyIssue(
                template = PrintTemplate.FREEFORM,
                key = null,
                weight = null,
                tag = key,
                content = "")
        tasksContainer.add(taskPlannerView)
        return taskPlannerView
    }

    fun removeFocusedTask() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
