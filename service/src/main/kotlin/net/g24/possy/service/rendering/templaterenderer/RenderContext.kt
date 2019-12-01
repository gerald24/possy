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
package net.g24.possy.service.rendering.templaterenderer

import net.g24.possy.service.PossyConfigurationProperties.PdfGenerator.Pages.PageSpec

class RenderContext(pageSpec: PageSpec, val headerFont: FontContext, val contentFont: FontContext, val footerFont: FontContext) {

    companion object {
        private const val POINTS_PER_INCH = 72f
        private const val POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH
    }

    val width: Float
    val height: Float
    val allowedTextWidth: Float
    val marginBorder: Float
    var cursor: Cursor

    init {
        val marginText = pageSpec.margin * POINTS_PER_MM

        width = pageSpec.width * POINTS_PER_MM
        height = pageSpec.height * POINTS_PER_MM
        marginBorder = pageSpec.border * POINTS_PER_MM
        allowedTextWidth = width - 2 * marginText
        cursor = Cursor(marginText, height - marginText)
    }
}
