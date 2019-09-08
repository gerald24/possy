package net.g24.possy.service.security

import com.vaadin.flow.server.VaadinSession
import net.g24.possy.service.logger
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Logout from multiple open browser tabs/windows
 */
internal class VaadinSessionClosingLogoutHandler : LogoutHandler {

    override fun logout(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication?) {
        logger().debug("closing all Vaadin sessions")
        VaadinSession.getAllSessions(request.session).forEach { vaadinSession ->
            logger().debug("closing Vaadin session $vaadinSession")
            vaadinSession.lock()
            try {
                vaadinSession.close()
            } finally {
                vaadinSession.unlock()
            }
        }
    }
}
