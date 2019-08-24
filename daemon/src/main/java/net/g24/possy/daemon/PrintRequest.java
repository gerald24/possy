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

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author: Gerald Leeb
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrintRequest {

    private UUID id;
    private PrintTemplate template;
    private String header;
    private byte[] content;
    private String mimetype;

    public UUID getId() {
        return id;
    }

    public PrintTemplate getTemplate() {
        return template;
    }

    public String getHeader() {
        return header;
    }

    public byte[] getContent() {
        return content;
    }

    public String getMimetype() {
        return mimetype;
    }

    public String toString() {
        return String.format("%s (%s) %s [%s]",
                id,
                template,
                header == null || header.isEmpty() ? "<freeform>" : header,
                mimetype == null ? "text" : mimetype);
    }

}
