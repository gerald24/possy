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
package net.g24.possy.service.rendering

import net.g24.possy.service.PossyConfigurationProperties
import net.g24.possy.service.PossyConfigurationProperties.PdfGenerator.Pages.PageSpec
import net.g24.possy.service.model.PossyIssue
import net.g24.possy.service.model.PrintTemplate
import net.g24.possy.service.rendering.templaterenderer.*
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Service
class PdfGenerator(private val possyProperties: PossyConfigurationProperties, private val resourceLoader: ResourceLoader) {

    fun createPdf(possyIssue: PossyIssue): ByteArray {
        ByteArrayOutputStream().use { stream ->
            writeToStream(possyIssue, stream, true)
            return stream.toByteArray()
        }
    }

    fun createImage(possyIssue: PossyIssue): ByteArray {
        ByteArrayOutputStream().use { stream ->
            writeToStream(possyIssue, stream, false)
            PDDocument.load(stream.toByteArray()).use { doc ->
                val image = PDFRenderer(doc).renderImage(0, 2.0f, ImageType.ARGB)
                // make white area transparten?
                ByteArrayOutputStream().use { imageStream ->
                    ImageIO.write(image, "png", imageStream)
                    return imageStream.toByteArray()
                }
            }
        }
    }

    private fun writeToStream(possyIssue: PossyIssue, stream: ByteArrayOutputStream, rotateIfNeeded: Boolean) {
        when (possyIssue.template) {
            PrintTemplate.STORY -> generatePdf(possyIssue, stream, possyProperties.pdfGenerator.pages.story, StoryTemplateRenderer(), rotateIfNeeded)
            PrintTemplate.TASK -> generatePdf(possyIssue, stream, possyProperties.pdfGenerator.pages.task, TaskTemplateRenderer(), rotateIfNeeded)
            PrintTemplate.FREEFORM -> generatePdf(possyIssue, stream, possyProperties.pdfGenerator.pages.freeform, FreeformTemplateRenderer(), rotateIfNeeded)
            PrintTemplate.BUG -> generatePdf(possyIssue, stream, possyProperties.pdfGenerator.pages.bug, DefaultTemplateRenderer(), rotateIfNeeded)
            //PrintTemplate.IMAGE ->  // TODO
        }
    }

    private fun generatePdf(
            possyIssue: PossyIssue,
            stream: ByteArrayOutputStream,
            pageSpec: PageSpec,
            layoutRenderer: LayoutRenderer,
            rotateIfNeeded: Boolean) {
        PDDocument().use { doc ->
            val renderContext = RenderContext(
                    pageSpec,
                    FontContext(resourceLoader, possyProperties.pdfGenerator.fonts.header),
                    FontContext(resourceLoader, possyProperties.pdfGenerator.fonts.content),
                    FontContext(resourceLoader, possyProperties.pdfGenerator.fonts.footer))
            val page = PDPage()
            page.mediaBox = PDRectangle(renderContext.width, renderContext.height)
            if (rotateIfNeeded && renderContext.width > renderContext.height) {
                page.rotation = 90
            }
            doc.addPage(page)
            PDPageContentStream(doc, page).use { contents -> layoutRenderer.render(possyIssue, doc, contents, renderContext) }
            doc.save(stream)
        }
    }
}
