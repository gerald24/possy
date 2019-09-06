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

import net.g24.possy.daemon.PrintRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class DefaultTemplateRenderer extends TemplateRenderer {

    @Override
    public void render(
            final PrintRequest printRequest, final PDDocument doc, final PDPageContentStream contents, final RenderContext renderContext)
            throws IOException {
        renderIssueAsHeader(printRequest, doc, contents, renderContext);
        renderContent(printRequest, doc, contents, renderContext);
        drawLightRect(contents, renderContext.getWidth(), renderContext.getHeight(), renderContext.getMarginBorder());
        renderWeight(printRequest, doc, contents, renderContext);
        renderTag(printRequest, doc, contents, renderContext);
    }

}
