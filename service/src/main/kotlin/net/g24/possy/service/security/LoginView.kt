package net.g24.possy.service.security

import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.polymertemplate.PolymerTemplate
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.ParentLayout
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InitialPageSettings
import com.vaadin.flow.server.PageConfigurator
import com.vaadin.flow.templatemodel.TemplateModel
import net.g24.possy.service.ui.PwaRootLayout
import org.springframework.beans.factory.annotation.Value

@Tag("login-view")
@Route("login")
@PageTitle("Possy Login")
@JsModule("./src/login-view.js")
@ParentLayout(PwaRootLayout::class)
class LoginView(@Value("\${spring.application.name}") val appName: String)
    : PolymerTemplate<LoginView.LoginViewModel>(), PageConfigurator {

    init {
        model.setAppName(appName)
    }

    interface LoginViewModel : TemplateModel {
        fun setAppName(appName: String)
    }

    override fun configurePage(settings: InitialPageSettings) {
        // dirty hack to unshadow the shadow dom in order to allow password managers (e.g. LastPass) to function properly
        // see https://stackoverflow.com/questions/53361884/how-to-use-browserautocomplete-with-vaadin-10-textfield
        settings.addInlineWithContents(
                InitialPageSettings.Position.PREPEND,
                "if (window.customElements) window.customElements.forcePolyfill = true; ShadyDOM = { force: true };",
                InitialPageSettings.WrapMode.JAVASCRIPT
        )
    }
}
