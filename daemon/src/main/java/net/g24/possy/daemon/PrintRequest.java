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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.nio.charset.Charset;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PrintRequest {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private UUID id;
    private PrintTemplate template;
    private String key;
    private String weight;
    private String tag;
    private byte[] content;
    private String mimetype;

    public UUID getId() {
        return id;
    }

    public PrintTemplate getTemplate() {
        return template;
    }

    public String getKey() {
        return key;
    }

    public boolean hasKey() {
        return key != null && key.length() > 0;
    }

    public String getWeight() {
        return weight;
    }

    public boolean hasWeight() {
        return weight != null && weight.length() > 0;
    }

    public String getTag() {
        return tag;
    }

    public boolean hasTag() {
        return tag != null && tag.length() > 0;
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentAsString() {
        return new String(content, CHARSET);
    }

    public String getMimetype() {
        return mimetype;
    }

    public String toString() {
        return String.format("%s (%s) %s [%s]",
                             id,
                             template,
                             key == null || key.isEmpty() ? "<no issue>" : key,
                             mimetype == null ? "text" : mimetype);
    }
}
