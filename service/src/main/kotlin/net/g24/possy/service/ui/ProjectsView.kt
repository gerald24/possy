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
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteAlias
import com.vaadin.flow.router.RouterLink
import net.g24.possy.service.jira.JiraProject
import net.g24.possy.service.jira.JiraService
import net.g24.possy.service.ui.components.asComponent

/**
 * @author: Gerald Leeb
 */
@Route("", layout = MainLayout::class)
@RouteAlias("projects", layout = MainLayout::class)
@PageTitle("Possy Projects")
class ProjectsView(val jiraService: JiraService) : VerticalLayout() {

    init {
        addClassName("possy-projects")
        jiraService.projects.forEach { add(asLinkComponent(it)) }
    }

    private fun asLinkComponent(project: JiraProject): Component {
        return RouterLink(null, ProjectView::class.java, project.key)
                .apply {
                    jiraService.projectImages[project.key].asComponent(project.key)?.let { add(it) }
                    add(Span("${project.key} (${project.name})")
                            .apply { addClassName("jira-project-title") })
                    addClassName("jira-project")
                }

    }


}
