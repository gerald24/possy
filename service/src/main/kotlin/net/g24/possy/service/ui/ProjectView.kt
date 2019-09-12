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
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.Route
import net.g24.possy.service.extensions.asFormatted
import net.g24.possy.service.jira.JiraService
import net.g24.possy.service.model.PossyIssue
import net.g24.possy.service.model.PossyProject
import net.g24.possy.service.ui.components.PossyIssueComponent
import net.g24.possy.service.ui.components.asComponent
import java.time.LocalDateTime

@Route("projects/project", layout = MainLayout::class)
class ProjectView(
        private val jiraService: JiraService,
        private val printRequestCreation: PrintRequestCreation,
        private val pageTitleBuilder: PageTitleBuilder
) : VerticalLayout(), HasUrlParameter<String>, HasDynamicTitle {

    private val projectHeader = Div().apply { addClassName("jira-project-header") }
    private val updatedInfo = Span()
    private val updateTriggerButton = Button("Update") { loadIssues() }
    private val issuesContainer = VerticalLayout().apply { isMargin = false; isPadding = false }

    var project: PossyProject? = null

    init {
        addClassName("possy-project")
        updatedInfo.addClassName("jira-recent-timestamp")
        issuesContainer.element.style.set("overflow-y", "auto")
        add(projectHeader)
        addAndExpand(issuesContainer)
    }

    override fun getPageTitle(): String = pageTitleBuilder.build(project?.key ?: "Project")

    override fun setParameter(event: BeforeEvent, parameter: String) {
        val projects = jiraService.projects
        if (projects == null) {
            removeAll()
            add(Paragraph("Error loading projects!"))
            return
        }

        project = projects.firstOrNull { it.key.equals(parameter, ignoreCase = true) }
        if (project == null) {
            removeAll()
            add(Paragraph("Unknown project $parameter!"))
            return
        }
        projectHeader.apply {
            removeAll()
            project!!.avatar.asComponent(project!!.key)?.let { add(it) }
            add(Span("${project!!.key} (${project!!.name})").apply { addClassName("jira-project-title") })
            add(updateTriggerButton, updatedInfo)
        }
        loadIssues()
    }

    private fun loadIssues() {
        project ?: return
        try {
            updateIssues(jiraService.loadRecentIssues(project!!.key))
        } catch (e: Exception) {
            e.printStackTrace()
            showError(e.toString())
        }
    }

    private fun updateIssues(recentIssues: List<PossyIssue>) {
        issuesContainer.removeAll()
        project ?: return

        recentIssues.forEach { issuesContainer.add(PossyIssueComponent(it) { issue -> printRequestCreation.confirm(issue) }) }
        updatedInfo.text = "Loaded: ${LocalDateTime.now().asFormatted()}"
    }

    private fun showError(message: String) {
        issuesContainer.removeAll()
        project ?: return
        updatedInfo.text = "Error: $message (${LocalDateTime.now().asFormatted()})"
    }
}
