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
import java.util.Arrays;
import java.util.stream.Stream;

import net.g24.possy.daemon.LayoutRenderer;
import net.g24.possy.daemon.PrintRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;

public class TemplateRenderer implements LayoutRenderer {

    @Override
    public void render(
            final PrintRequest printRequest, final PDDocument doc, final PDPageContentStream contents, final RenderContext renderContext)
            throws IOException {
        renderIssueAsHeader(printRequest, doc, contents, renderContext);
        renderContent(printRequest, doc, contents, renderContext);
        renderWeight(printRequest, doc, contents, renderContext);
        renderTag(printRequest, doc, contents, renderContext);
    }

    protected void renderContent(
            final PrintRequest printRequest, final PDDocument doc, final PDPageContentStream contents, final RenderContext renderContext)
            throws IOException {

        FontContext contentFont = renderContext.getContentFont();
        renderContext.getCursor().down(contentFont.getSize());
        drawMultiLineText(
                contents,
                printRequest.getContentAsString(),
                renderContext.getCursor(),
                renderContext.getAllowedTextWidth(),
                contentFont.getFont(doc),
                contentFont.getSize(),
                contentFont.getLineHeight());

        drawRect(contents, renderContext.getWidth(), renderContext.getHeight(), renderContext.getMarginBorder());
    }

    protected void renderIssueAsHeader(
            final PrintRequest printRequest, final PDDocument doc, final PDPageContentStream contents, final RenderContext renderContext)
            throws IOException {
        if (printRequest.hasIssue()) {
            FontContext headerFont = renderContext.getHeaderFont();
            renderContext.getCursor().down(headerFont.getSize());
            showTextAt(contents, printRequest.getIssue(), renderContext.getCursor(), headerFont.getFont(doc), headerFont.getSize());
            renderContext.getCursor().down(headerFont.getLineHeight() - headerFont.getSize());
        }
    }

    protected void renderWeight(
            final PrintRequest printRequest, final PDDocument doc, final PDPageContentStream contents, final RenderContext renderContext)
            throws IOException {

        if (!printRequest.hasWeight()) {
            return;
        }
        String text = printRequest.getWeight();

        FontContext headerFont = renderContext.getHeaderFont();
        PDTrueTypeFont font = headerFont.getFont(doc);
        int fontSize = headerFont.getSize();

        float textWidth = getTextWidth(text, font, fontSize);

        contents.setStrokingColor(100, 100, 100);
        contents.setLineWidth(0.5f);
        float hBorder = 3.0f;
        float vBorder = (headerFont.getLineHeight() - headerFont.getSize()) / 2.0f;
        float baseline = fontSize / 6.0f; // how to calc baseline?
        float w = textWidth + 2 * hBorder;
        float h = headerFont.getLineHeight();
        float x = renderContext.getWidth() - renderContext.getMarginBorder() - w;
        float y = renderContext.getHeight() - renderContext.getMarginBorder() - h;
        contents.addRect(x, y, w, h);
        contents.stroke();

        contents.setNonStrokingColor(0, 0, 0);
        showTextAt(contents, text, new Cursor(x + hBorder, y + vBorder + baseline), font, fontSize);
    }

    protected void renderTag(
            final PrintRequest printRequest, final PDDocument doc, final PDPageContentStream contents, final RenderContext renderContext)
            throws IOException {
        if (!printRequest.hasTag()) {
            return;
        }

        String text = printRequest.getTag();
        FontContext footerFont = renderContext.getFooterFont();
        PDTrueTypeFont font = footerFont.getFont(doc);
        int fontSize = footerFont.getSize();

        float textWidth = getTextWidth(text, font, fontSize);

        contents.setNonStrokingColor(180, 180, 180);
        float hBorder = 4.0f;
        float vBorder = (footerFont.getLineHeight() - footerFont.getSize()) / 2.0f;
        float baseline = fontSize / 6.0f; // how to calc baseline?
        float w = textWidth + 2 * hBorder;
        float h = footerFont.getLineHeight();
        float x = renderContext.getWidth() - 2 * renderContext.getMarginBorder() - w;
        float y = 2 * renderContext.getMarginBorder();
        contents.addRect(x, y, w, h);
        contents.fill();

        contents.setNonStrokingColor(255, 255, 255);
        showTextAt(contents, text, new Cursor(x + hBorder, y + vBorder + baseline), font, fontSize);
        contents.setNonStrokingColor(0, 0, 0);

    }

    protected void drawRect(final PDPageContentStream contents, final float width, final float height, final float marginBorder) throws IOException {
        contents.setStrokingColor(100, 100, 100);
        contents.setLineWidth(0.5f);
        contents.addRect(marginBorder, marginBorder, width - 2 * marginBorder, height - 2 * marginBorder);
        contents.stroke();
    }

    protected void drawMultiLineText(
            final PDPageContentStream contentStream,
            final String text,
            final Cursor cursor,
            final float allowedWidth,
            final PDFont font,
            final float fontSize,
            final float lineHeight) throws IOException {

        if (text == null || text.trim().length() == 0) {
            return;
        }

        showTextLines(contentStream, splitAndFitIntoMultiLines(text, allowedWidth, font, fontSize), cursor, font, fontSize, lineHeight);
    }

    private Stream<String> splitAndFitIntoMultiLines(final String text, final float allowedWidth, final PDFont font, final float fontSize) {
        return Arrays.stream(text.trim().split("\n")).flatMap(paragraph -> splitParagraph(paragraph, allowedWidth, font, fontSize));
    }

    private Stream<String> splitParagraph(final String line, final float allowedWidth, final PDFont font, final float fontSize) {
        Stream.Builder<String> builder = Stream.builder();

        String[] words = line.trim().split(" ");
        StringBuilder lineBuilder = new StringBuilder();
        for (String word : words) {
            if (lineBuilder.length() > 0) {
                lineBuilder.append(" ");
            }

            float size = getTextWidth(lineBuilder.toString() + word, font, fontSize);
            if (size > allowedWidth) {
                if (lineBuilder.length() > 0) {
                    if (getTextWidth(word, font, fontSize) > allowedWidth) {
                        String concat = lineBuilder.toString() + word;
                        lineBuilder.delete(0, lineBuilder.length());
                        splitWord(concat, allowedWidth, font, fontSize, builder, lineBuilder);
                    } else {
                        builder.accept(lineBuilder.toString());
                        lineBuilder.replace(0, lineBuilder.length() - 1, word);
                    }
                } else {
                    splitWord(word, allowedWidth, font, fontSize, builder, lineBuilder);
                }
            } else {
                lineBuilder.append(word);
            }
        }
        builder.accept(lineBuilder.toString());

        return builder.build();
    }

    private void splitWord(
            final String word,
            final float allowedWidth,
            final PDFont font,
            final float fontSize,
            final Stream.Builder<String> builder,
            final StringBuilder lineBuilder) {
        String wordNotFit = word;
        while (!wordNotFit.isEmpty()) {
            int idx = wordNotFit.length();
            while (idx > 0) {
                String part = wordNotFit.substring(0, idx);
                float size = getTextWidth(part, font, fontSize);
                if (size > allowedWidth) {
                    idx--;
                } else {
                    if (idx == wordNotFit.length()) {
                        lineBuilder.append(part);
                        return;
                    } else {
                        builder.accept(part);
                        wordNotFit = wordNotFit.substring(idx);
                        idx = 0;
                    }
                }
            }
        }
    }

    protected float getTextWidth(final String text, final PDFont font, final float fontSize) {
        try {
            return fontSize * font.getStringWidth(text) / 1000.0f;
        } catch (IOException e) {
            throw new RuntimeException("error evaluate text width for text " + text, e);
        }
    }

    private void showTextLines(
            final PDPageContentStream contentStream,
            final Stream<String> lines,
            final Cursor cursor,
            final PDFont font,
            final float fontSize,
            final float lineHeight) {
        lines.forEach(line -> {
            showTextAt(contentStream, line, cursor, font, fontSize);
            cursor.down(lineHeight);
        });
    }

    protected void showTextAt(
            final PDPageContentStream contentStream, final String line, final Cursor cursor, final PDFont font, final float fontSize) {
        if (cursor.y < 0) {
            return;
        }
        try {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(cursor.x, cursor.y);
            contentStream.showText(line);
            contentStream.endText();
        } catch (IOException e) {
            throw new RuntimeException("error while rendering line " + line, e);
        }
    }

}
