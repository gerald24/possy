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
package net.g24.possy.service.ui.components

import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.polymertemplate.EventHandler
import com.vaadin.flow.component.polymertemplate.PolymerTemplate
import com.vaadin.flow.templatemodel.TemplateModel
import net.g24.possy.service.jira.JiraIssue

/**
 * @author: Gerald Leeb
 */
@Tag("possy-jira-issue")
@JsModule("./src/possy-jira-issue.js")
class PossyJiraIssue(val jiraIssue: JiraIssue, val clickHandler: (jiraIssue: JiraIssue) -> Unit) : PolymerTemplate<PossyJiraIssue.PossyJiraIssueModel>() {

    interface PossyJiraIssueModel : TemplateModel {
        fun setKey(key: String)
        fun setSummary(summary: String)
        fun setType(type: String)
    }

    init {
        setId(jiraIssue.key)
        model.setKey(jiraIssue.key)
        model.setSummary(jiraIssue.summary)
        model.setType(jiraIssue.issueTypeName)
    }

    @EventHandler
    private fun handleClick() {
        clickHandler.invoke(jiraIssue)
    }
}
