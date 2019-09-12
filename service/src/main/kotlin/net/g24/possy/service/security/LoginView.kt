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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.info.GitProperties
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Tag("login-view")
@Route("login")
@PageTitle("Possy Login")
@JsModule("./src/login-view.js")
@ParentLayout(PwaRootLayout::class)
class LoginView(
        @Value("\${spring.application.name}") appName: String,
        @Autowired gitProperties: GitProperties
) : PolymerTemplate<LoginView.LoginViewModel>(), PageConfigurator {

    init {
        val combinedVersion = gitProperties.get("commit.id.describe");
        val buildTime = gitProperties.getInstant("build.time")
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime()
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        model.setAppName(appName)
        model.setAppVersionInfo("Version $combinedVersion, $buildTime")
    }

    interface LoginViewModel : TemplateModel {
        fun setAppName(appName: String)
        fun setAppVersionInfo(appVersionInfo: String)
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
