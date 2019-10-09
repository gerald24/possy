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
package net.g24.possy.daemon

import net.g24.possy.daemon.configuration.PossyProperties
import net.g24.possy.daemon.templaterenderer.*
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream

@Component
class PdfGenerator(private val possyProperties: PossyProperties, private val resourceLoader: ResourceLoader) {

    fun createPdf(printRequest: PrintRequest): ByteArray {
        ByteArrayOutputStream().use { stream ->
            when (printRequest.template) {
                PrintTemplate.STORY -> generatePdf(printRequest, stream, possyProperties.pdfGenerator.pages.story, StoryTemplateRenderer())
                PrintTemplate.TASK -> generatePdf(printRequest, stream, possyProperties.pdfGenerator.pages.task, TaskTemplateRenderer())
                PrintTemplate.FREEFORM -> generatePdf(printRequest, stream, possyProperties.pdfGenerator.pages.freeform, FreeformTemplateRenderer())
                PrintTemplate.BUG -> generatePdf(printRequest, stream, possyProperties.pdfGenerator.pages.bug, DefaultTemplateRenderer())
                PrintTemplate.IMAGE -> stream // TODO
            }
            return stream.toByteArray()
        }
    }

    private fun generatePdf(
            printRequest: PrintRequest,
            stream: ByteArrayOutputStream,
            pageSpec: PossyProperties.PdfGenerator.Pages.PageSpec,
            layoutRenderer: LayoutRenderer) {
        PDDocument().use { doc ->
            val renderContext = RenderContext(
                    pageSpec,
                    FontContext(resourceLoader, possyProperties.pdfGenerator.fonts.header),
                    FontContext(resourceLoader, possyProperties.pdfGenerator.fonts.content),
                    FontContext(resourceLoader, possyProperties.pdfGenerator.fonts.footer))
            val page = PDPage()
            page.mediaBox = PDRectangle(renderContext.width, renderContext.height)
            if (renderContext.width > renderContext.height) {
                page.rotation = 90
            }
            doc.addPage(page)
            PDPageContentStream(doc, page).use { contents -> layoutRenderer.render(printRequest, doc, contents, renderContext) }
            doc.save(stream)
        }
    }
}
