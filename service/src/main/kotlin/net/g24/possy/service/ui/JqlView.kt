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

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyPressEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.*
import net.g24.possy.service.jira.JiraService
import net.g24.possy.service.model.PossyProject
import net.g24.possy.service.ui.components.PossyIssueComponent

@Route("jql", layout = MainLayout::class)
class JqlView(
        private val jiraService: JiraService,
        private val printRequestCreation: PrintRequestCreation,
        private val pageTitleBuilder: PageTitleBuilder
) : VerticalLayout(), HasUrlParameter<String>, HasDynamicTitle {

    private val projectSelector = ComboBox<String>()
    private val searchField = TextField()
    private val resultContainer = VerticalLayout()
    private val projects: List<PossyProject>?

    init {
        addClassName("possy-jql")

        projects = jiraService.projects
        if (projects == null) {
            add(Paragraph("Error loading projects!"))
        } else {
            initProjectSelector(projects.map { it.key })
            initSearchField()
            initResultContainer()

            add(projectSelector, searchField)
            addAndExpand(resultContainer)
        }
    }

    override fun getPageTitle(): String = pageTitleBuilder.build("JQL")

    override fun setParameter(event: BeforeEvent, @OptionalParameter parameter: String?) {
        if (projects == null) {
            return
        }
        parameter?.let {
            val project = parameter.trim().toLowerCase()
            projects.find { it.key.toLowerCase() == project }?.let { projectSelector.value = it.key }
            event.location.queryParameters.parameters["jql"]?.let { searchField.value = it.firstOrNull() ?: "" }
        }
    }

    override fun onAttach(attachEvent: AttachEvent?) {
        super.onAttach(attachEvent)
        pushCurrentUrlState()
    }

    private fun initProjectSelector(projectKeys: List<String>) {
        projectSelector.setItems(projectKeys)
        projectSelector.value = projectKeys.firstOrNull()
        projectSelector.isAllowCustomValue = false
        projectSelector.isRequired = true
        projectSelector.addValueChangeListener {
            pushCurrentUrlState()
            searchChanged()
        }
    }

    private fun pushCurrentUrlState() {
        ui.ifPresent { ui ->
            var parameter = projectSelector.value
            if (!searchField.value.isNullOrBlank()) {
                parameter += "?jql=${searchField.value.trim()}"
            }
            val newUrl = RouteConfiguration.forSessionScope().getUrl(JqlView::class.java, parameter)
            ui.page.history.pushState(null, newUrl)
        }
    }

    private fun initSearchField() {
        searchField.width = "100%"
        searchField.placeholder = "Key or JQL, Press ENTER to submit"
        searchField.title = "Key (eg. 12 or BMP-12) or JQL (e.g. key=BMP-12 or summary~'Betrieb'), Press ENTER to submit"
        searchField.valueChangeMode = ValueChangeMode.ON_CHANGE
        searchField.isClearButtonVisible = true
        searchField.addValueChangeListener { searchChanged() }
        searchField.addKeyPressListener(Key.ESCAPE, ComponentEventListener<KeyPressEvent> { searchField.value = "" })
    }

    private fun initResultContainer() {
        resultContainer.setWidthFull()
        resultContainer.element.style.set("overflow-y", "auto")
    }


    private fun searchChanged() {
        pushCurrentUrlState()
        resultContainer.removeAll()
        val searchCriteria = searchField.value.trim()
        if (searchCriteria.isBlank()) {
            return
        }
        try {
            val issues = jiraService.findIssues(projectSelector.value, searchCriteria)

            if (issues.isEmpty()) {
                resultContainer.add("no issue found.")
                return
            }
            val size = issues.size
            if (size == 1) {
                resultContainer.add(PossyIssueComponent(issues.first()) { printRequestCreation.confirm(it) })
                return
            }
            issues.forEach { issue -> resultContainer.add(PossyIssueComponent(issue) { printRequestCreation.confirm(it) }) }
            resultContainer.add(Button("Print all $size issues", VaadinIcon.PRINT.create()) { printRequestCreation.printAll(issues) })
        } catch (e: Exception) {
            resultContainer.add("Error caught. JQL correct?")
        }

    }

}
