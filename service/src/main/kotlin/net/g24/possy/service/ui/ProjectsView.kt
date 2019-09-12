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

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteAlias
import com.vaadin.flow.router.RouterLink
import net.g24.possy.service.extensions.ResettableLazyManager
import net.g24.possy.service.jira.JiraService
import net.g24.possy.service.model.PossyProject
import net.g24.possy.service.ui.components.asComponent

@Route("", layout = MainLayout::class)
@RouteAlias("projects", layout = MainLayout::class)
class ProjectsView(
        private val jiraService: JiraService,
        private val resettableLazyManager: ResettableLazyManager,
        private val pageTitleBuilder: PageTitleBuilder
) : VerticalLayout(), HasDynamicTitle {

    private val resetAndLoadButton = Button("Reset") { resetAndLoad() }

    init {
        addClassName("possy-projects")
        load()
    }

    override fun getPageTitle(): String = pageTitleBuilder.build("Projects")

    private fun load() {
        removeAll()
        val projects = jiraService.projects
        if (projects == null) {
            add(Paragraph("Error loading projects!"))
        } else {
            projects.forEach { add(asLinkComponent(it)) }
        }
        add(resetAndLoadButton)
    }

    private fun resetAndLoad() {
        resettableLazyManager.reset()
        load()
    }

    private fun asLinkComponent(project: PossyProject): Component {
        return RouterLink(null, ProjectView::class.java, project.key)
                .apply {
                    project.avatar.asComponent(project.key)?.let { add(it) }
                    add(Span("${project.key} (${project.name})")
                            .apply { addClassName("jira-project-title") })
                    addClassName("jira-project")
                }

    }


}
