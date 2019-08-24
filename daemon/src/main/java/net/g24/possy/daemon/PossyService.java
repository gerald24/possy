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

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// TODO https://github.com/gerald24/possy/issues/3

/**
 * @author: Gerald Leeb
 */
@Component
public class PossyService {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String host;
    private final int port;
    private final String printerWhite;
    private final String printerPink;
    private final String printerYellow;
    private final boolean redirectToFile;
    private final PdfGenerator pdfGenerator;

    private int jobId = 0;
    private CupsClient client;

    public PossyService(
            @Value("${cups.host}") String host,
            @Value("${cups.port}") int port,
            @Value("${cups.printer.white}") String printerWhite,
            @Value("${cups.printer.pink}") String printerPink,
            @Value("${cups.printer.yellow}") String printerYellow,
            @Value("${redirect_to_file}") boolean redirectToFile,
            @Autowired PdfGenerator pdfGenerator) {
        this.host = host;
        this.port = port;
        this.printerWhite = printerWhite;
        this.printerPink = printerPink;
        this.printerYellow = printerYellow;
        this.redirectToFile = redirectToFile;
        this.pdfGenerator = pdfGenerator;
    }

    @PostConstruct
    public void setUpCupsClient() throws Exception {
        client = new CupsClient(host, port);
        try {
            listAllPrinters(client);
        } catch (Exception e) {
            // ignore
        }
    }

    public void print(final PrintTemplate template, final String header, final byte[] content, final String mimetype) throws Exception {
        if (template == PrintTemplate.IMAGE) {
            printImage(header, content, mimetype);
        } else {
            printDocument(template, header, new String(content, CHARSET));
        }
    }

    private void printImage(final String header, final byte[] content, final String mimetype) {
        // TODO implement (https://github.com/gerald24/possy/issues/4)
    }

    private void printDocument(final PrintTemplate template, final String header, final String content) throws Exception {
        byte[] out = pdfGenerator.createPdf(template, header, content);

        final Map<String, String> attribs = new HashMap<>();
        attribs.put("document-format", "application/pdf");

        if (redirectToFile) {
            Files.write(Files.createTempFile("possy", ".pdf").toAbsolutePath(), out);
        } else {
            CupsPrinter cupsPrinter = getCupsPrinter(template);
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
                return printerWhite;
            case PINK:
                return printerPink;
            case YELLOW:
                return printerYellow;
        }
        // default
        return printerPink;
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
