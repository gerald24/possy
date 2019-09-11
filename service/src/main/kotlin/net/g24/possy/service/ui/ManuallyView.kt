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

@Route("manually", layout = MainLayout::class)
@PageTitle("Possy Manually")
class ManuallyView(private val printRequestCreation: PrintRequestCreation) : VerticalLayout(), HasUrlParameter<String> {
    private val ctrlOrMeta: KeyModifier = KeyModifier.valueOf("CONTROL")
    private val printTemplateSelector = ComboBox<PrintTemplate>()
    private val printButton = Button("Print", VaadinIcon.PRINT.create()) { queueIssue() }
    private val header = TextField()
    private val weight = TextField()
    private val content = TextArea()
    private val tag = TextField()

    init {
        setSizeFull()
        addClassName("possy-freeform")

        initProjectTemplateSelector()
        initHeader()
        initWeight()
        initContent()
        initTag()

        add(
                HorizontalLayout().apply {
                    add(printTemplateSelector, printButton)
                    isSpacing = true
                },
                HorizontalLayout().apply {
                    addAndExpand(header)
                    add(weight, tag)
                }
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
        initField(header, "Header")
    }


    private fun initWeight() {
        initField(weight, "Weight")
    }

    private fun initContent() {
        content.setSizeFull()
        content.placeholder = "Content (press Print-Button or CTRL-ENTER to print)"
        content.valueChangeMode = ValueChangeMode.EAGER
        content.isClearButtonVisible = true
        content.addValueChangeListener { updateAppearance() }
        content.addKeyPressListener(Key.ESCAPE, ComponentEventListener<KeyPressEvent> { content.value = "" })
        content.addKeyPressListener(Key.ENTER, ComponentEventListener<KeyPressEvent> { queueIssue() }, ctrlOrMeta)
    }

    private fun initTag() {
        initField(tag, "Tag")
    }


    private fun initField(field: TextField, placeholder: String) {
        field.placeholder = placeholder
        field.valueChangeMode = ValueChangeMode.EAGER
        field.isClearButtonVisible = true
        field.addValueChangeListener { updateAppearance() }
        field.addKeyPressListener(Key.ESCAPE, ComponentEventListener<KeyPressEvent> { field.value = "" })
        field.addKeyPressListener(Key.ENTER, ComponentEventListener<KeyPressEvent> { queueIssue() }, ctrlOrMeta)
    }


    private fun updateAppearance() {
        header.isVisible = hasHeader()
        weight.isVisible = hasWeight()
        tag.isVisible = hasTag()
        printButton.isEnabled = (!header.isVisible || header.isVisible && header.value.isNotBlank()) && content.value.isNotBlank()
    }

    private fun queueIssue() {
        if (!printButton.isEnabled) {
            return
        }
        printRequestCreation.printUnconfirmed(
                PossyIssue(
                        printTemplateSelector.value,
                        if (!hasHeader()) "" else header.value,
                        if (!hasWeight()) null else weight.value,
                        if (!hasTag()) null else tag.value,
                        content.value
                )
        )
        header.value = ""
        weight.value = ""
        content.value = ""
        tag.value = ""

        updateFocus()
    }

    private fun hasTag() = printTemplateSelector.value == PrintTemplate.STORY || printTemplateSelector.value == PrintTemplate.FREEFORM

    private fun hasWeight() = printTemplateSelector.value == PrintTemplate.STORY

    private fun hasHeader() = printTemplateSelector.value != PrintTemplate.FREEFORM

    private fun updateFocus() {
        if (printTemplateSelector.value == PrintTemplate.FREEFORM) {
            content.focus()
        } else {
            header.focus()
        }
    }
}
