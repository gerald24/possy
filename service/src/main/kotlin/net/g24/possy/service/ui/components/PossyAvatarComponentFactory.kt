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

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import net.g24.possy.service.model.PossyAvatar
import java.io.ByteArrayInputStream

fun PossyAvatar?.asComponent(projectKey: String): Component? {
    if (this == null)
        return null
    if (contentType == "png") {
        return Image(
                StreamResource(projectKey, InputStreamFactory { ByteArrayInputStream(content) }),
                "Avatar $projectKey)")
                .apply { addClassName("jira-project-avatar") }
    }
    if (contentType == "svg") {
        return Span().apply {
            element.setProperty("innerHTML", String(content))
            addClassName("jira-project-avatar")
        }
    }
    return null
}
