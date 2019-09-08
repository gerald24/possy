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

import net.g24.possy.daemon.configuration.PossyProperties;

public class RenderContext {

    private static final float POINTS_PER_INCH = 72;
    private static final float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;
    private final float width;
    private final float height;
    private final float allowedTextWidth;
    private final float marginBorder;
    private final float marginText;
    private final FontContext headerFontContext;
    private final FontContext contentFontContext;
    private final FontContext footerFontContext;
    private final Cursor cursor;

    public RenderContext(
            final PossyProperties.PdfGenerator.Pages.PageSpec pageSpec,
            final FontContext headerFontContext,
            final FontContext contentFontContext,
            final FontContext footerFontContext) {
        this.width = pageSpec.getWidth() * POINTS_PER_MM;
        this.height = pageSpec.getHeight() * POINTS_PER_MM;
        this.marginBorder = pageSpec.getBorder() * POINTS_PER_MM;
        this.marginText = pageSpec.getMargin() * POINTS_PER_MM;
        this.headerFontContext = headerFontContext;
        this.contentFontContext = contentFontContext;
        this.footerFontContext = footerFontContext;
        this.allowedTextWidth = width - 2 * marginText;
        this.cursor = new Cursor(marginText, height - marginText);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getMarginBorder() {
        return marginBorder;
    }

    public float getMarginText() {
        return marginText;
    }

    public float getAllowedTextWidth() {
        return allowedTextWidth;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public FontContext getHeaderFont() {
        return headerFontContext;
    }

    public FontContext getContentFont() {
        return contentFontContext;
    }

    public FontContext getFooterFont() {
        return footerFontContext;
    }
}
