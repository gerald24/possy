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

import net.g24.possy.service.ui.PwaRootLayout
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import javax.annotation.PostConstruct

@SpringBootApplication
class PossyServiceApplication(@Value("\${spring.application.name}") private val appName: String) {

    @PostConstruct
    @Suppress("kotlin:S1144", "unused")
    private fun init() { //NOSONAR
        PwaAnnotationModifier.dynamicPwaAnnotation(PwaRootLayout::class.java, appName, appName)
    }
}

fun main(args: Array<String>) {
    System.setProperty("spring.devtools.restart.enabled", "false")
    runApplication<PossyServiceApplication>(*args)
}
