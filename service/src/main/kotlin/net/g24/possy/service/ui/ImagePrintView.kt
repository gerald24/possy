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
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route

// TODO https://github.com/gerald24/possy/issues/4 implement me

@Route("image", layout = MainLayout::class)
class ImagePrintView(private val pageTitleBuilder: PageTitleBuilder) : VerticalLayout(), HasDynamicTitle {

    private var buffer = MultiFileBuffer()
    private val upload = Upload(buffer)
    private val printButton = Button("Print", VaadinIcon.PRINT.create()) { queueImage() }

    init {
        addClassName("possy-image-print")
        add(H2("Image Print"))

        // TODO handle file cleanup, resizing, ....

        printButton.isEnabled = false
        upload.isDropAllowed = true
        upload.maxFileSize = 10_000_000
        upload.dropLabel = Paragraph("Drop Picture here")
        upload.maxFiles = 1
        upload.width = "90%"
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.addSucceededListener {
            Notification.show("Upload succeeded", 4000, Notification.Position.MIDDLE)
            printButton.isEnabled = true
        }

        add(upload, printButton)
    }

    override fun getPageTitle(): String = pageTitleBuilder.build("Image")

    private fun queueImage() {
    }

}
