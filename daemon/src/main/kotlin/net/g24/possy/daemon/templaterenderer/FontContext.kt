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
package net.g24.possy.daemon.templaterenderer

import net.g24.possy.daemon.configuration.PossyProperties.PdfGenerator.Fonts.FontSpec
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding
import org.springframework.core.io.ResourceLoader

class FontContext(private val resourceLoader: ResourceLoader, private val fontSpec: FontSpec) {

    private lateinit var font: PDTrueTypeFont

    val size: Int
        get() = fontSpec.size

    val lineHeight: Int
        get() = fontSpec.lineHeight

    fun applyFont(doc: PDDocument): PDTrueTypeFont {
        if (!::font.isInitialized) {
            font = addFont(doc, fontSpec.font!!)
        }
        return font
    }

    private fun addFont(doc: PDDocument, name: String): PDTrueTypeFont {
        resourceLoader.getResource(name).inputStream.use {
            return PDTrueTypeFont.load(doc, it, WinAnsiEncoding.INSTANCE)
        }
    }
}
