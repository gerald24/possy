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

import net.g24.possy.daemon.LayoutRenderer
import net.g24.possy.daemon.PrintRequest
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import rst.pdfbox.layout.shape.RoundRect
import rst.pdfbox.layout.text.Position

abstract class TemplateRenderer : LayoutRenderer {

    protected fun renderContent(printRequest: PrintRequest, doc: PDDocument, contents: PDPageContentStream, renderContext: RenderContext) {
        val contentFont = renderContext.contentFont
        renderContext.cursor = renderContext.cursor.down(contentFont.size.toFloat())
        drawMultiLineText(
                contents,
                printRequest.contentAsString,
                renderContext,
                contentFont.applyFont(doc),
                contentFont.size.toFloat(),
                contentFont.lineHeight.toFloat())
    }

    protected fun renderIssue(printRequest: PrintRequest, doc: PDDocument, contents: PDPageContentStream, renderContext: RenderContext) {
        if (printRequest.key.isNullOrBlank()) {
            return
        }

        val headerFont = renderContext.headerFont
        renderContext.cursor = renderContext.cursor.down(headerFont.size.toFloat())
        showTextAt(contents, printRequest.key, renderContext.cursor, headerFont.applyFont(doc), headerFont.size.toFloat())
        renderContext.cursor = renderContext.cursor.down((headerFont.lineHeight - headerFont.size).toFloat())
    }

    protected fun renderWeight(printRequest: PrintRequest, doc: PDDocument, contents: PDPageContentStream, renderContext: RenderContext) {
        if (printRequest.weight.isNullOrBlank()) {
            return
        }

        val text = printRequest.weight

        val headerFont = renderContext.headerFont
        val font = headerFont.applyFont(doc)
        val fontSize = headerFont.size

        val textWidth = getTextWidth(text, font, fontSize.toFloat())

        contents.setStrokingColor(100, 100, 100)
        contents.setLineWidth(0.5f)
        val hBorder = 3.0f
        val vBorder = (headerFont.lineHeight - headerFont.size) / 2.0f
        val baseline = fontSize / 6.0f // how to calc baseline?
        val w = textWidth + 2 * hBorder
        val h = headerFont.lineHeight.toFloat()
        val x = renderContext.width - renderContext.marginBorder - w
        val y = renderContext.height - renderContext.marginBorder - h
        contents.addRect(x, y, w, h)
        contents.stroke()

        contents.setNonStrokingColor(0, 0, 0)
        showTextAt(contents, text, Cursor(x + hBorder, y + vBorder + baseline), font, fontSize.toFloat())
    }

    protected fun renderTag(printRequest: PrintRequest, doc: PDDocument, contents: PDPageContentStream, renderContext: RenderContext) {
        if (printRequest.tag.isNullOrBlank()) {
            return
        }

        val text = printRequest.tag
        val footerFont = renderContext.footerFont
        val font = footerFont.applyFont(doc)
        val fontSize = footerFont.size

        val textWidth = getTextWidth(text, font, fontSize.toFloat())

        contents.setNonStrokingColor(100, 100, 100)
        val hBorder = 6.0f
        val vBorder = (footerFont.lineHeight - footerFont.size) / 2.0f
        val baseline = fontSize / 6.0f // how to calc baseline?
        val w = textWidth + 2 * hBorder
        val h = footerFont.lineHeight.toFloat()
        val x = renderContext.width - 2 * renderContext.marginBorder - w
        val y = 2 * renderContext.marginBorder
        RoundRect(hBorder).add(doc, contents, Position(x, y + h), w, h)
        contents.fill()

        contents.setNonStrokingColor(255, 255, 255)
        showTextAt(contents, text, Cursor(x + hBorder, y + vBorder + baseline), font, fontSize.toFloat())
        contents.setNonStrokingColor(0, 0, 0)

    }

    protected fun drawLightRect(contents: PDPageContentStream, width: Float, height: Float, marginBorder: Float) {
        contents.setStrokingColor(100, 100, 100)
        contents.setLineWidth(0.5f)
        contents.addRect(marginBorder, marginBorder, width - 2 * marginBorder, height - 2 * marginBorder)
        contents.stroke()
    }

    protected fun drawBoldRect(contents: PDPageContentStream, width: Float, height: Float, marginBorder: Float) {
        contents.setStrokingColor(0, 0, 0)
        contents.setLineWidth(2f)
        contents.addRect(marginBorder, marginBorder, width - 2 * marginBorder, height - 2 * marginBorder)
        contents.stroke()
    }

    private fun drawMultiLineText(
            contentStream: PDPageContentStream,
            text: String?,
            renderContext: RenderContext,
            font: PDFont,
            fontSize: Float,
            lineHeight: Float) {

        if (text.isNullOrBlank()) {
            return
        }

        showTextLines(contentStream, splitAndFitIntoMultiLines(text, renderContext.allowedTextWidth, font, fontSize), renderContext, font, fontSize, lineHeight)
    }

    private fun splitAndFitIntoMultiLines(text: String, allowedWidth: Float, font: PDFont, fontSize: Float): List<String> {
        return text.trim().split("\n").flatMap { splitParagraph(it, allowedWidth, font, fontSize) }
    }

    private fun splitParagraph(line: String, allowedWidth: Float, font: PDFont, fontSize: Float): List<String> {
        val result = mutableListOf<String>()

        val words = line.trim().split(" ")
        val lineBuilder = StringBuilder()
        for (word in words) {
            if (lineBuilder.isNotEmpty()) {
                lineBuilder.append(" ")
            }

            val size = getTextWidth(lineBuilder.toString() + word, font, fontSize)
            if (size > allowedWidth) {
                if (lineBuilder.isNotEmpty()) {
                    if (getTextWidth(word, font, fontSize) > allowedWidth) {
                        val concat = lineBuilder.toString() + word
                        lineBuilder.delete(0, lineBuilder.length)
                        splitWord(concat, allowedWidth, font, fontSize, result, lineBuilder)
                    } else {
                        result.add(lineBuilder.toString())
                        lineBuilder.replace(0, lineBuilder.length - 1, word)
                    }
                } else {
                    splitWord(word, allowedWidth, font, fontSize, result, lineBuilder)
                }
            } else {
                lineBuilder.append(word)
            }
        }
        result.add(lineBuilder.toString())
        return result.toList()
    }

    private fun splitWord(
            word: String,
            allowedWidth: Float,
            font: PDFont,
            fontSize: Float,
            result: MutableList<String>,
            lineBuilder: StringBuilder) {
        var wordNotFit = word
        while (wordNotFit.isNotEmpty()) {
            var idx = wordNotFit.length
            while (idx > 0) {
                val part = wordNotFit.substring(0, idx)
                val size = getTextWidth(part, font, fontSize)
                if (size > allowedWidth) {
                    idx--
                } else {
                    if (idx == wordNotFit.length) {
                        lineBuilder.append(part)
                        return
                    } else {
                        result.add(part)
                        wordNotFit = wordNotFit.substring(idx)
                        idx = 0
                    }
                }
            }
        }
    }

    private fun getTextWidth(text: String, font: PDFont, fontSize: Float): Float =
            fontSize * font.getStringWidth(text) / 1000.0f

    private fun showTextLines(
            contentStream: PDPageContentStream,
            lines: List<String>,
            renderContext: RenderContext,
            font: PDFont,
            fontSize: Float,
            lineHeight: Float) {
        lines.forEach { line ->
            showTextAt(contentStream, line, renderContext.cursor, font, fontSize)
            renderContext.cursor = renderContext.cursor.down(lineHeight)
        }
    }

    protected fun showTextAt(contentStream: PDPageContentStream, line: String, cursor: Cursor, font: PDFont, fontSize: Float) {
        if (cursor.y < 0) {
            return
        }

        contentStream.beginText()
        contentStream.setFont(font, fontSize)
        contentStream.newLineAtOffset(cursor.x, cursor.y)
        contentStream.showText(line)
        contentStream.endText()
    }
}
