package net.g24.possy.service.security

import com.vaadin.flow.server.ServletHelper.RequestType
import com.vaadin.flow.shared.ApplicationConstants
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Stream
import javax.servlet.http.HttpServletRequest

object SecurityUtils {

    val username: String
        get() {
            val context = SecurityContextHolder.getContext()
            val userDetails = context.authentication.principal as UserDetails
            return userDetails.username
        }

    val isUserLoggedIn: Boolean
        get() {
            val context = SecurityContextHolder.getContext()
            return context.authentication != null && context.authentication !is AnonymousAuthenticationToken
        }


    internal fun isFrameworkInternalRequest(request: HttpServletRequest): Boolean {
        val parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER)
        return parameterValue != null && Stream.of(*RequestType.values()).anyMatch { r -> r.identifier == parameterValue }
    }

}
