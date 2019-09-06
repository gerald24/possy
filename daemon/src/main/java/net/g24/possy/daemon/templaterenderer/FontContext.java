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
package net.g24.possy.daemon.templaterenderer;

import java.io.IOException;

import net.g24.possy.daemon.configuration.PossyProperties;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;

public class FontContext {

    private final PossyProperties.PdfGenerator.Fonts.FontSpec fontSpec;
    private PDTrueTypeFont font;

    public FontContext(final PossyProperties.PdfGenerator.Fonts.FontSpec fontSpec) {
        this.fontSpec = fontSpec;
    }


    public int getSize() {
        return fontSpec.getSize();
    }

    public int getLineHeight() {
        return fontSpec.getLineHeight();
    }


    public PDTrueTypeFont getFont(final PDDocument doc) {
        if (font == null) {
            font = addFont(doc, fontSpec.getFont());
        }
        return font;
    }

    private PDTrueTypeFont addFont(final PDDocument doc, final String name) {
        try {
            return PDTrueTypeFont.load(doc, getClass().getClassLoader().getResourceAsStream(name), WinAnsiEncoding.INSTANCE);
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException("error adding font " + name, e);
        }
    }
}
