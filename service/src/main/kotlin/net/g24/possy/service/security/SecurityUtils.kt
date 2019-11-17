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
            return context.authentication != null
                    && context.authentication !is AnonymousAuthenticationToken
                    && context.authentication.isAuthenticated
        }

    internal fun isFrameworkInternalRequest(request: HttpServletRequest): Boolean {
        val parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER)
        return parameterValue != null && Stream.of(*RequestType.values()).anyMatch { r -> r.identifier == parameterValue }
    }

}
