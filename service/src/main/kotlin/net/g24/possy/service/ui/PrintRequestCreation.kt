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

import com.vaadin.flow.component.confirmdialog.ConfirmDialog
import com.vaadin.flow.component.notification.Notification
import net.g24.possy.service.configuration.JiraConfiguration
import net.g24.possy.service.model.PossyIssue
import net.g24.possy.service.service.PrintRequestQueueService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author: Gerald Leeb
 */
@Component
class PrintRequestCreation @Autowired constructor(
        val printRequestQueueService: PrintRequestQueueService,
        val jiraConfiguration: JiraConfiguration) {

    fun confirm(issue: PossyIssue) {
        val dialog = ConfirmDialog(
                "Confirm Print",
                "Print issue ${issue.key}?",
                "Print", {
            printUnconfirmed(issue)
        },
                "Cancel", {})
        dialog.open()
    }

    fun printAll(issues: List<PossyIssue>) {
        issues.forEach { printRequestQueueService.addItem(it) }
        showNotification()
    }

    fun printUnconfirmed(printRequest: PossyIssue) {
        printRequestQueueService.addItem(printRequest)
        showNotification()
    }

    private fun showNotification() {
        Notification.show("added to print queued", 600, Notification.Position.MIDDLE)
    }


}
