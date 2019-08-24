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

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import net.g24.possy.service.extensions.asFormatted
import net.g24.possy.service.jira.JiraIssue
import net.g24.possy.service.jira.JiraService
import net.g24.possy.service.ui.components.PossyJiraIssue
import java.time.LocalDateTime

/**
 * @author: Gerald Leeb
 */
@Route("project", layout = MainLayout::class)
@PageTitle("Possy Project")
class ProjectView(val jiraService: JiraService, val printRequestCreation: PrintRequestCreation) : VerticalLayout(), HasUrlParameter<String> {

    val projectHeader = H2("Project").apply { element.style.set("margin", "0 0 0 10px") }
    val updatedInfo = Span()
    val updateTriggerButton = Button("Update") { loadIssues() }
    val issuesContainer = VerticalLayout().apply { isMargin = false; isPadding = false }
    var project: String? = null


    init {
        addClassName("possy-project")
        updatedInfo.addClassName("jira-recent-timestamp")
        issuesContainer.element.style.set("overflow-y", "auto")

        add(HorizontalLayout(projectHeader, updatedInfo, updateTriggerButton).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.BASELINE
            isMargin = false
        })
        addAndExpand(issuesContainer)
    }

    override fun setParameter(event: BeforeEvent, parameter: String) {
        project = parameter
        projectHeader.text = "Project $project"
        loadIssues()
    }

    private fun loadIssues() {
        project ?: return
        try {
            updateIssues(jiraService.loadRecentIssues(project!!))
        } catch (e: Exception) {
            showError(e.toString())
        }
    }

    private fun updateIssues(recentIssues: List<JiraIssue>) {
        issuesContainer.removeAll()
        project ?: return

        recentIssues.forEach { issuesContainer.add(PossyJiraIssue(it) { issue -> printRequestCreation.confirm(issue) }) }
        updatedInfo.text = "Loaded: ${LocalDateTime.now().asFormatted()}"
    }

    private fun showError(message: String) {
        issuesContainer.removeAll()
        project ?: return
        updatedInfo.text = "Error: $message (${LocalDateTime.now().asFormatted()})"
    }
}
