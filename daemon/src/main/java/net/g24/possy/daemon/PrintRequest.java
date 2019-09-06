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
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author: Gerald Leeb
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrintRequest {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private UUID id;
    private PrintTemplate template;
    private String issue;
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

    public String getIssue() {
        return issue;
    }
    public boolean hasIssue() {
        return issue != null && issue.length() > 0;
    }

    public String getWeight() {
        return weight;
    }

    public boolean hasWeight() {return weight != null && weight.length() > 0;
    }

    public String getTag() {
        return tag;
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
                             issue == null || issue.isEmpty() ? "<no issue>" : issue,
                             mimetype == null ? "text" : mimetype);
    }
}
