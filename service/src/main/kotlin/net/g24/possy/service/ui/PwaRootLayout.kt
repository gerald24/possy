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
@PWA(name = "Possy", shortName = "Possy")
@Theme(Lumo::class)
@CssImport("./styles/shared-styles.css")
class PwaRootLayout : RouterLayout {

    private val div = Div()

    override fun getElement(): Element = div.element
}
