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

import net.g24.possy.daemon.configuration.PossyProperties;
import net.g24.possy.daemon.templaterenderer.DefaultTemplateRenderer;
import net.g24.possy.daemon.templaterenderer.FontContext;
import net.g24.possy.daemon.templaterenderer.FreeformTemplateRenderer;
import net.g24.possy.daemon.templaterenderer.RenderContext;
import net.g24.possy.daemon.templaterenderer.StoryTemplateRenderer;
import net.g24.possy.daemon.templaterenderer.TaskTemplateRenderer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// TODO https://github.com/gerald24/possy/issues/3

/**
 * @author: Gerald Leeb
 */
@Component
public class PdfGenerator {

    private final PossyProperties possyProperties;

    @Autowired
    public PdfGenerator(PossyProperties possyProperties) {
        this.possyProperties = possyProperties;
    }

    public byte[] createPdf(final PrintRequest printRequest) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        switch (printRequest.getTemplate()) {
            case STORY:
                generatePdf(printRequest, stream, possyProperties.getPdfGenerator().getPages().getStory(), new StoryTemplateRenderer());
                break;
            case TASK:
                generatePdf(printRequest, stream, possyProperties.getPdfGenerator().getPages().getTask(), new TaskTemplateRenderer());
                break;
            case FREEFORM:
                generatePdf(printRequest, stream, possyProperties.getPdfGenerator().getPages().getFreeform(), new FreeformTemplateRenderer());
                break;
            default:
                generatePdf(printRequest, stream, possyProperties.getPdfGenerator().getPages().getBug(), new DefaultTemplateRenderer());
                break;
        }

        return stream.toByteArray();
    }

    private void generatePdf(
            final PrintRequest printRequest,
            final ByteArrayOutputStream stream,
            final PossyProperties.PdfGenerator.Pages.PageSpec pageSpec,
            final LayoutRenderer layoutRenderer) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            RenderContext renderContext = new RenderContext(
                    pageSpec,
                    new FontContext(possyProperties.getPdfGenerator().getFonts().getHeader()),
                    new FontContext(possyProperties.getPdfGenerator().getFonts().getContent()),
                    new FontContext(possyProperties.getPdfGenerator().getFonts().getFooter()));
            PDPage page = new PDPage();
            page.setMediaBox(new PDRectangle(renderContext.getWidth(), renderContext.getHeight()));
            if (renderContext.getWidth() > renderContext.getHeight()) {
                page.setRotation(90);
            }
            doc.addPage(page);
            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                layoutRenderer.render(printRequest, doc, contents, renderContext);
            }
            doc.save(stream);
        }
    }

}
