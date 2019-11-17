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

import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.dom.Element
import com.vaadin.flow.router.RouterLayout
import com.vaadin.flow.server.PWA
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo

@Push
@PWA(name = "Possy", shortName = "Possy") // names will be replaced automatically with dynamic content, see PwaAnnotationModifier.java
@Theme(Lumo::class)
@CssImport("./styles/shared-styles.css")
class PwaRootLayout : RouterLayout {

    private val div = Div().apply { setSizeFull() }

    override fun getElement(): Element = div.element
}
