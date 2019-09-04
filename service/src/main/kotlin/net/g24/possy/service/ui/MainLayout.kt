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

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.TabVariant
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.router.*
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import net.g24.possy.service.model.PrintRequest
import net.g24.possy.service.service.PrintRequestQueueService
import net.g24.possy.service.ui.components.PossyPrintRequestItem
import org.springframework.beans.factory.annotation.Value
import java.util.*

/**
 * @author: Gerald Leeb
 */
@Push
@CssImport.Container(
        CssImport(value = "./styles/shared-styles.css"),
        CssImport(value = "./styles/vaadin-app-layout-drawer-right.css", themeFor = "vaadin-app-layout")
)
@Theme(value = Lumo::class)
class MainLayout(@Value("\${spring.application.name}") val appName: String, val printRequestQueueService: PrintRequestQueueService) : AppLayout(), RouterLayout, AfterNavigationObserver {
    private var dark = false
    private val themeToggleButton = Button("Switch to") { toggleThemeVariant() }
    private val tabs = Tabs()
    private val navEntries = mutableMapOf<Class<out Component>, Tab>()
    private val printQueueLayout = VerticalLayout()
    private val noIssuesParagraph = Paragraph("No Issues to print.")
    private var listenerDeregistration = Runnable {}
    private val printQueueChangedListener = object : PrintRequestQueueService.Listener {
        override fun itemAdded(request: PrintRequest) {
            ui.ifPresent { it.access { this@MainLayout.itemAdded(request) } }
        }

        override fun itemConsumed(request: PrintRequest) {
            ui.ifPresent { it.access { this@MainLayout.itemConsumed(request) } }
        }

        override fun itemRemoved(request: PrintRequest) {
            ui.ifPresent { it.access { this@MainLayout.itemRemoved(request) } }
        }
    }

    init {
        initNavigationBar()
        initDrawerWithPrintQueue()
    }

    override fun afterNavigation(event: AfterNavigationEvent) {
        val segments = event.location.segments
        val path = if (segments.isNullOrEmpty()) "" else segments[0]
        tabs.selectedTab = RouteConfiguration.forSessionScope().getRoute(path)
                .map { navEntries[it] }
                .orElse(null)
    }


    private fun initNavigationBar() {
        val branding = initBranding()

        tabs.orientation = Tabs.Orientation.HORIZONTAL;
        addTopNavigation(tabs)

        addToNavbar(true, branding, tabs)
        addToNavbar(DrawerToggle())
    }

    private fun initBranding(): H1 {
        val appName = H1(appName)
        appName.addClassName("app-name")
        appName.element.style.set("margin", "0")
        appName.element.style.set("padding-left", "0.25em")
        appName.addClassName("hide-on-mobile")
        return appName
    }

    private fun addTopNavigation(tabs: Tabs) {
        tabs.add(createTab(ProjectsView::class.java, VaadinIcon.TAGS, "Projects"))
        tabs.add(createTab(JqlView::class.java, VaadinIcon.SEARCH, "JQL"))
        tabs.add(createTab(ManuallyView::class.java, VaadinIcon.TEXT_INPUT, "Manually"))
        // TODO https://github.com/gerald24/possy/issues/4
        // tabs.add(createTab(ImagePrintView::class.java, VaadinIcon.PICTURE, "Image Print"))
        tabs.add(createTab(PlannerView::class.java, VaadinIcon.BULLETS, "Planner"))
    }

    override fun onAttach(attachEvent: AttachEvent?) {
        super.onAttach(attachEvent)
        matchThemeVariant()
        listenerDeregistration.run()
        listenerDeregistration = printRequestQueueService.addListener(printQueueChangedListener)
    }

    override fun onDetach(detachEvent: DetachEvent?) {
        listenerDeregistration.run()
        listenerDeregistration = Runnable {}
        super.onDetach(detachEvent)
    }

    private fun createTab(component: Class<out Component>, icon: VaadinIcon, text: String): Tab {
        val link = RouterLink(null, component)
        link.add(icon.create())
        link.add(text)

        val tab = Tab()
        tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP)
        tab.add(link)

        navEntries[component] = tab

        return tab
    }

    private fun initDrawerWithPrintQueue() {
        printQueueLayout.setSizeFull()
        printQueueLayout.addClassName("print-queue-layout")
        printQueueLayout.isMargin = false

        val layout = VerticalLayout()
        layout.addClassName("print-queue-view")
        layout.add(themeToggleButton)
        layout.add(H3("Queue:"))
        layout.add(noIssuesParagraph);
        layout.addAndExpand(printQueueLayout)
        layout.setSizeFull()
        layout.isSpacing = false

        addToDrawer(layout)
        showItems()
    }

    private fun matchQueueEntriesVisibility() {
        val isEmpty = printRequestQueueService.allItems().isEmpty()
        noIssuesParagraph.isVisible = isEmpty
        printQueueLayout.isVisible = !isEmpty
    }

    private fun showItems() {
        printQueueLayout.removeAll()
        matchQueueEntriesVisibility()
        printRequestQueueService.allItems().forEach { printQueueLayout.add(PossyPrintRequestItem(it)) }
    }

    private fun itemAdded(request: PrintRequest) {
        matchQueueEntriesVisibility()
        printQueueLayout.add(PossyPrintRequestItem(request))
    }

    private fun itemConsumed(request: PrintRequest) {
        matchQueueEntriesVisibility()
        findMatchingComponent(request).ifPresent { c -> c.setConsumed(request.isConsumed) }
    }

    private fun itemRemoved(request: PrintRequest) {
        matchQueueEntriesVisibility()
        findMatchingComponent(request).ifPresent { c -> remove(c) }
    }

    private fun findMatchingComponent(request: PrintRequest): Optional<PossyPrintRequestItem> {
        return printQueueLayout.children
                .filter { c -> c is PossyPrintRequestItem }
                .map { c -> c as PossyPrintRequestItem }
                .filter { c -> c.id.isPresent && c.id.get() == request.id.toString() }
                .findFirst()
    }

    private fun toggleThemeVariant() {
        dark = !dark
        matchThemeVariant()
    }

    private fun matchThemeVariant() {
        UI.getCurrent().element.setAttribute("theme", if (dark) Lumo.DARK else Lumo.LIGHT);
        themeToggleButton.element.text = "Switch to ${if (dark) "light" else "dark"} theme"
    }
}
