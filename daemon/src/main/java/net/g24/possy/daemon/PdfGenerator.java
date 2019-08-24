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
package net.g24.possy.daemon;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

// TODO https://github.com/gerald24/possy/issues/3

/**
 * @author: Gerald Leeb
 */
@Component
public class PdfGenerator {

    private static final float POINTS_PER_INCH = 72;
    private static final float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;
    private final String headerFontName;
    private final String contentFontName;

    private static class Cursor {

        float x;
        float y;

        Cursor(final float x, final float y) {
            this.x = x;
            this.y = y;
        }

        void down(final float offset) {
            y = y - offset;
        }
    }

    @Autowired
    public PdfGenerator(
            @Value("${possy.pdf.font.header}") String headerFont,
            @Value("${possy.pdf.font.content}") String contentFont) {
        this.headerFontName = headerFont;
        this.contentFontName = contentFont;
    }

        public byte[] createPdf(final PrintTemplate template, final String header, final String content) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        switch (template) {
            case STORY:
                // page height usually 72.0f but for TM88IV lower
                generatePdf(header, content, stream, 160.0f, 64.0f);
                break;
            case TASK:
                // page height usually 72.0f but for TM88IV lower
                generatePdf(header, content, stream, 64.0f, 64.0f);
                break;
            default:
                generatePdf(header, content, stream, 72.0f, 80.0f);
                break;
        }

        return stream.toByteArray();
    }

    private void generatePdf(
            final String header, final String content, final ByteArrayOutputStream stream, final float pageWidthInMM, final float pageHeightInMM)
            throws IOException {
        PDDocument doc = new PDDocument();
        try {
            float width = pageWidthInMM * POINTS_PER_MM;
            float height = pageHeightInMM * POINTS_PER_MM;
            float marginBorder = 1 * POINTS_PER_MM;
            float marginText = 3 * POINTS_PER_MM;
            float cap = 3 * POINTS_PER_MM;
            float headerFontSize = 26;
            float contentFontSize = 18;
            float allowedTextWidth = width - 2 * marginText;
            Cursor cursor = new Cursor(marginText, height - marginText);

            PDPage page = new PDPage();
            page.setMediaBox(new PDRectangle(width, height));
            if (width > height) {
                page.setRotation(90);
            }
            doc.addPage(page);

            PDPageContentStream contents = new PDPageContentStream(doc, page);
            if (header != null && header.length() > 0) {
                PDTrueTypeFont headerFont = addFont(doc, headerFontName);
                cursor.down(headerFontSize);
                showTextAt(contents, header, cursor, headerFont, headerFontSize);
                cursor.down(cap);
            }

            PDFont contentFont = addFont(doc, contentFontName);
            cursor.down(contentFontSize);
            drawMultiLineText(contents, content, cursor, allowedTextWidth, contentFont, 16, 20);

            drawRect(contents, width, height, marginBorder);
            contents.close();

            doc.save(stream);
        } finally {
            doc.close();
        }
    }

    private void drawRect(final PDPageContentStream contents, final float width, final float height, final float marginBorder) throws IOException {
        contents.setStrokingColor(100, 100, 100);
        contents.setLineWidth(0.5f);
        contents.addRect(marginBorder, marginBorder, width - 2 * marginBorder, height - 2 * marginBorder);
        contents.stroke();
    }

    private void drawMultiLineText(
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

    private float getTextWidth(final String text, final PDFont font, final float fontSize) {
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

    private void showTextAt(
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

    private PDTrueTypeFont addFont(final PDDocument doc, final String name) throws IOException {
        return PDTrueTypeFont.load(doc, getClass().getClassLoader().getResourceAsStream(name), WinAnsiEncoding.INSTANCE);
    }
}
