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

import com.vaadin.flow.component.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.*
import net.g24.possy.service.model.PossyIssue
import net.g24.possy.service.model.PrintTemplate

/**
 * @author: Gerald Leeb
 */
@Route("manually", layout = MainLayout::class)
@PageTitle("Possy Manually")
class ManuallyView(val printRequestCreation: PrintRequestCreation) : VerticalLayout(), HasUrlParameter<String> {
    private val ctrlOrMeta: KeyModifier = KeyModifier.valueOf("CONTROL")
    private val printTemplateSelector = ComboBox<PrintTemplate>()
    private val printButton = Button("Print", VaadinIcon.PRINT.create()) { queueIssue() }
    private val header = TextField()
    private val content = TextArea()

    init {
        setSizeFull()
        addClassName("possy-freeform")

        initProjectTemplateSelector()
        initHeader()
        initContent()

        add(
                HorizontalLayout().apply {
                    add(printTemplateSelector, printButton)
                    isSpacing = true
                },
                header
        )
        addAndExpand(content)

        updateAppearance()
        updateFocus()
    }

    override fun setParameter(event: BeforeEvent, @OptionalParameter parameter: String?) {
        parameter?.let {
            val template = parameter.trim().toUpperCase()
            PrintTemplate.values().find { it.name.toUpperCase() == template }?.let { printTemplateSelector.value = it }
        }
    }

    override fun onAttach(attachEvent: AttachEvent?) {
        super.onAttach(attachEvent)
        pushCurrentUrlState()
    }

    private fun initProjectTemplateSelector() {
        val templates = PrintTemplate.values().sorted().stream().filter { it != PrintTemplate.IMAGE }
        printTemplateSelector.width = "220px"
        printTemplateSelector.setItems(templates)
        printTemplateSelector.value = PrintTemplate.FREEFORM
        printTemplateSelector.isAllowCustomValue = false
        printTemplateSelector.isRequired = true
        printTemplateSelector.itemLabelGenerator = ItemLabelGenerator<PrintTemplate> { "$it (${it.printer})" }
        printTemplateSelector.addValueChangeListener {
            pushCurrentUrlState()
            updateAppearance()
            updateFocus()
        }
    }

    private fun pushCurrentUrlState() {
        ui.ifPresent { ui ->
            val newUrl = RouteConfiguration.forSessionScope().getUrl(ManuallyView::class.java, printTemplateSelector.value.name)
            ui.page.history.pushState(null, newUrl)
        }
    }

    private fun initHeader() {
        header.width = "100%"
        header.placeholder = "Header"
        header.valueChangeMode = ValueChangeMode.LAZY
        header.isClearButtonVisible = true
        header.addValueChangeListener { updateAppearance() }
        header.addKeyPressListener(Key.ESCAPE, ComponentEventListener<KeyPressEvent> { header.value = "" })
        header.addKeyPressListener(Key.ENTER, ComponentEventListener<KeyPressEvent> { queueIssue() }, ctrlOrMeta)
    }

    private fun initContent() {
        content.setSizeFull()
        content.placeholder = "Content (press Print-Button or CTRL-ENTER to print)"
        content.valueChangeMode = ValueChangeMode.LAZY
        content.isClearButtonVisible = true
        content.addValueChangeListener { updateAppearance() }
        content.addKeyPressListener(Key.ESCAPE, ComponentEventListener<KeyPressEvent> { header.value = "" })
        content.addKeyPressListener(Key.ENTER, ComponentEventListener<KeyPressEvent> { queueIssue() }, ctrlOrMeta)
    }


    private fun updateAppearance() {
        header.isVisible = printTemplateSelector.value != PrintTemplate.FREEFORM
        printButton.isEnabled = (!header.isVisible || header.isVisible && header.value.isNotBlank()) && content.value.isNotBlank()
    }

    private fun queueIssue() {
        if (!printButton.isEnabled) {
            return
        }
        printRequestCreation.printUnconfirmed(
                PossyIssue(
                        printTemplateSelector.value,
                        if (printTemplateSelector.value == PrintTemplate.FREEFORM) "" else header.value,
                        null, null,
                        content.value
                )
        )
        header.value = ""
        content.value = ""
        updateFocus()
    }

    private fun updateFocus() {
        if (printTemplateSelector.value == PrintTemplate.FREEFORM) {
            content.focus()
        } else {
            header.focus()
        }
    }
}
