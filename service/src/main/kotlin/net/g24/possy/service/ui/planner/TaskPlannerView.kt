package net.g24.possy.service.ui.planner

import com.vaadin.flow.component.html.Span
import net.g24.possy.service.model.PossyIssue
import net.g24.possy.service.model.PrintTemplate
import net.g24.possy.service.ui.PdfPreviewView
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class TaskPlannerView(
        private val plannerSelectionEventAndActionBus: PlannerSelectionStateAndActionBus,
        private val pdfPreviewView: PdfPreviewView
) : Span() {

    var possyIssue: PossyIssue = PossyIssue(
            template = PrintTemplate.FREEFORM,
            key = null,
            weight = null,
            tag = null,
            content = "")
        get() = field
        set(value) {
            field = value
            render()
        }


    init {
        addClassNames("planner-task")
        add(pdfPreviewView)

        render()
        element.addEventListener("click") {
            plannerSelectionEventAndActionBus.selectTask(this)
        }
    }


    fun isNotEmpty() = possyIssue.content.isNotBlank()

    fun setContent(value: String) {
        possyIssue = PossyIssue(
                template = possyIssue.template,
                key = possyIssue.key,
                weight = possyIssue.weight,
                tag = possyIssue.tag,
                content = value)
        render()
    }


    private fun render() {
        pdfPreviewView.render(possyIssue)
        removeClassNames(*classNames.filter { it.startsWith("paper-") }.toTypedArray())
        addClassNames(
                "paper-type-${possyIssue.template.paper.name.toLowerCase()}",
                "paper-template-${possyIssue.template.name.toLowerCase()}"
        )
    }




}
