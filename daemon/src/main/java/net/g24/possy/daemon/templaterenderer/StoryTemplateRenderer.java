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

import net.g24.possy.daemon.PrintRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;

public class StoryTemplateRenderer extends TemplateRenderer {

	@Override
	public void render(
			final PrintRequest printRequest, final PDDocument doc, final PDPageContentStream contents, final RenderContext renderContext)
			throws IOException {
		renderIssueFX(printRequest, doc, contents, renderContext);
		renderIssue(printRequest, doc, contents, renderContext);
		renderContent(printRequest, doc, contents, renderContext);
		drawBoldRect(contents, renderContext.getWidth(), renderContext.getHeight(), renderContext.getMarginBorder());
		renderWeight(printRequest, doc, contents, renderContext);
		renderTag(printRequest, doc, contents, renderContext);
	}

	private void renderIssueFX(final PrintRequest printRequest, final PDDocument doc, final PDPageContentStream contents,
							   final RenderContext renderContext) throws IOException {
		if (printRequest.hasKey()) {
			String[] parts = printRequest.getKey().trim().split("-");
			String text = parts.length == 2 ? parts[1] : parts[0];

			FontContext headerFont = renderContext.getHeaderFont();

			contents.setNonStrokingColor(220, 220, 220);
			showTextAt(
					contents,
					text,
					new Cursor(renderContext.getMarginBorder(), renderContext.getMarginBorder()),
					headerFont.getFont(doc),
					renderContext.getHeight() / 1.5f);
			contents.setNonStrokingColor(0, 0, 0);
		}
	}
}
