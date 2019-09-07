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

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

import net.g24.possy.daemon.configuration.CupsProperties;
import net.g24.possy.daemon.configuration.PossyProperties;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: Gerald Leeb
 */
@Component
public class PossyService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CupsProperties cupsProperties;
    private final PossyProperties possyProperties;
    private final PdfGenerator pdfGenerator;

    private int jobId = 0;
    private CupsClient client;

    @Autowired
    public PossyService(
            CupsProperties cupsProperties, PossyProperties possyProperties, PdfGenerator pdfGenerator) {
        this.cupsProperties = cupsProperties;
        this.possyProperties = possyProperties;
        this.pdfGenerator = pdfGenerator;
    }

    @PostConstruct
    public void setUpCupsClient() throws Exception {
        client = new CupsClient(cupsProperties.getHost(), cupsProperties.getPort());
        try {
            listAllPrinters(client);
        } catch (Exception e) {
            // ignore
        }
    }

    public void print(final PrintRequest printRequest) throws Exception {
        if (printRequest.getTemplate() == PrintTemplate.IMAGE) {
            printImage(printRequest.getContent(), printRequest.getMimetype());
        } else {
            printDocument(printRequest);
        }
    }

    private void printImage(final byte[] content, final String mimetype) {
        // TODO implement (https://github.com/gerald24/possy/issues/4)
    }

    private void printDocument(final PrintRequest printRequest) throws Exception {
        byte[] out = pdfGenerator.createPdf(printRequest);

        final Map<String, String> attribs = new HashMap<>();
        attribs.put("document-format", "application/pdf");

        if (possyProperties.getPdfGenerator().isRedirectToFile()) {
            Files.write(Files.createTempFile("possy", ".pdf").toAbsolutePath(), out);
        } else {
            CupsPrinter cupsPrinter = getCupsPrinter(printRequest.getTemplate());
            org.cups4j.PrintJob pj = new PrintJob.Builder(out)
                    .jobName("Possy #" + ++jobId)
                    .attributes(attribs)
                    .userName("anonymous")
                    .copies(1)
                    .build();
            cupsPrinter.print(pj);
        }
    }

    private CupsPrinter getCupsPrinter(final PrintTemplate template) throws Exception {
        String printer = getPrinter(template);
        return client
                .getPrinters()
                .stream()
                .filter(p -> printer.equals(p.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("printer " + printer + " unknown"));
    }

    private String getPrinter(final PrintTemplate template) {
        switch (template.getPaper()) {
            case WHITE:
                return cupsProperties.getPrinters().getWhite();
            case PINK:
                return cupsProperties.getPrinters().getPink();
            case YELLOW:
                return cupsProperties.getPrinters().getYellow();
        }
        // default
        return cupsProperties.getPrinters().getPink();
    }

    private void listAllPrinters(CupsClient client) throws Exception {
        client.getPrinters().forEach(printer -> {
            logger.info("Printer: " + printer.toString());

            logger.debug(" Media supported:");
            printer.getMediaSupported().forEach(media -> logger.debug("  - " + media));

            logger.debug(" Resolution supported:");
            printer.getResolutionSupported().forEach(res -> logger.debug("  - " + res));

            logger.debug(" Mime-Types supported:");
            printer.getMimeTypesSupported().forEach(mime -> logger.debug("  - " + mime));
        });
    }

}
