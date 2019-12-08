package net.g24.possy.service.ui.planner


import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.data.value.ValueChangeMode

class TaskContentDialog(content: String, private val handler: (key: String) -> Unit) : Dialog() {

    private val contentField = TextArea().apply {
        width = "200px"
        height = "240px"
        value = content
        valueChangeMode = ValueChangeMode.EAGER
        addValueChangeListener { handler.invoke(it.value) }
    }

    init {
        add(contentField)
    }


    override fun open() {
        super.open()
        contentField.focus()
    }
}
