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
package net.g24.possy.service.model;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

// TODO (https://github.com/gerald24/possy/issues/5) convert to kotlin

/**
 * @author: Gerald Leeb
 */
public class PrintRequest {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private final UUID id = UUID.randomUUID();
    private final PrintTemplate template;
    private final String issue;
    private final String weight;
    private final String tag;
    private final String mimetype;
    private final byte[] content;
    private boolean consumed;

    public PrintRequest(
            final PrintTemplate template, final String issue, final String weight, final String tag, final String content) {
        this(template, issue, weight, tag, "text/plain", content.getBytes(CHARSET));
    }

    public PrintRequest(
            final PrintTemplate template, final String issue, final String weight, final String tag, final String mimetype, final byte[] content) {
        this.template = template;
        this.issue = issue;
        this.weight = weight;
        this.tag = tag;
        this.mimetype = mimetype;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public void markAsConsumed() {
        consumed = true;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public PrintTemplate getTemplate() {
        return template;
    }

    public String getIssue() {
        return issue;
    }

    public String getWeight() {
        return weight;
    }

    public String getTag() {
        return tag;
    }

    public byte[] getContent() {
        return content;
    }

    public String toString() {
        return issue;
    }

    public boolean isValid() {
        return template != null && (template == PrintTemplate.FREEFORM || StringUtils.isNotBlank(issue)) && content != null && content.length > 0;
    }

    public String getContentAsString() {
        return template == PrintTemplate.IMAGE || content == null ? "" : new String(content, CHARSET);
    }

    public boolean contentEquals(final PrintRequest other) {
        return Objects.equals(this.template, other.template) &&
                (this.template != PrintTemplate.FREEFORM && Objects.equals(this.issue, other.issue)) &&
                Objects.equals(this.mimetype, other.mimetype) &&
                Arrays.equals(this.content, other.content);
    }
}
