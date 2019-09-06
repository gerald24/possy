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
package net.g24.possy.service

import com.vaadin.flow.server.VaadinServlet
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import java.io.IOException
import java.util.*
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletResponse


/**
 * @author: Gerald Leeb
 */
@SpringBootApplication
class PossyServiceApplication : SpringBootServletInitializer()

fun main(args: Array<String>) {
    System.setProperty("spring.devtools.restart.enabled", "false")

    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Vienna"))
    Locale.setDefault(Locale("de", "AT"))

    runApplication<PossyServiceApplication>(*args)
}

/**
 * @author: Gerald Leeb
 */
@WebServlet(urlPatterns = ["/*"], asyncSupported = true)
class PossyServlet : VaadinServlet() {

    @Throws(ServletException::class, IOException::class)
    override fun service(req: ServletRequest, res: ServletResponse) {
        setAccessControlHeaders(res as HttpServletResponse)
        super.service(req, res)
    }

    private fun setAccessControlHeaders(resp: HttpServletResponse) {
        resp.setHeader("Access-Control-Allow-Methods", "*")
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type")
        resp.setHeader("Access-Control-Allow-Credentials", "true")
    }
}
