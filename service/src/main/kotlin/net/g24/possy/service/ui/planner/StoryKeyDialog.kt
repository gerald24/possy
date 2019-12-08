package net.g24.possy.service.ui.planner

import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyPressEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField

class StoryKeyDialog(private val handler: (key: String) -> Unit) : Dialog() {

    private val key = TextField().apply {
        label = "Key"
        maxLength = 30
        width = "100%"
        addKeyPressListener(Key.ENTER, ComponentEventListener<KeyPressEvent> { acceptIfNotEmpty() })
    }

    init {
        add(
                H3("Story Key"),
                key,
                HorizontalLayout(
                        Button("Close", VaadinIcon.CLOSE.create()) {
                            close()
                        },
                        Button("Add Story", VaadinIcon.CHECK.create()) {
                            acceptIfNotEmpty()
                        }
                ))

    }


    override fun open() {
        super.open()
        key.focus()
    }

    private fun acceptIfNotEmpty() {
        if (key.value.isNotBlank()) {
            handler.invoke(key.value.trim())
            close()
        } else {
            key.errorMessage = "Enter Key!"

        }
    }
}
