package net.g24.possy.service.ui

import com.vaadin.flow.component.html.Image
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import net.g24.possy.service.model.PossyIssue
import net.g24.possy.service.rendering.PdfGenerator
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class PdfPreviewView(private val pdfGenerator: PdfGenerator) : Image() {
    init {
        addClassName("pdf-preview")
        style.set("border", "none")

    }

    fun render(possyIssue: PossyIssue) {
        val image = pdfGenerator.createImage(possyIssue)
        val resource = StreamResource("preview.png", InputStreamFactory { ByteArrayInputStream(image) })
        resource.setContentType(MediaType.IMAGE_PNG_VALUE)
        element.setAttribute("src", resource)

    }


}
