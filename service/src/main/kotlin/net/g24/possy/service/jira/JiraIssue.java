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
package net.g24.possy.service.jira;

import net.g24.possy.service.model.PrintRequest;
import net.g24.possy.service.model.PrintTemplate;

// TODO (https://github.com/gerald24/possy/issues/5) convert to kotlin
// TODO find better solution for custom fields (e.g. story, epos)

/**
 * @author: Gerald Leeb
 */
public class JiraIssue {

    private String key;
    private JiraFields fields;

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public JiraFields getFields() {
        return fields;
    }

    public void setFields(final JiraFields fields) {
        this.fields = fields;
    }

    public PrintTemplate getTemplate() {
        String id = getIssueTypeId();

        // TODO https://github.com/gerald24/possy/issues/3
        if ("1".equals(id) || "10200".equals(id) || "10800".equals(id) || "11105".equals(id)) {
            return PrintTemplate.BUG;
        }
        if ("10101".equals(id) || "10201".equals(id)) {
            return PrintTemplate.STORY;
        }

        return PrintTemplate.TASK;
    }

    public String getIssueTypeId() {
        return fields != null ? fields.getIssuetypeId() : null;
    }

    public String getIssueTypeName() {
        return fields != null ? fields.getIssuetypeName() : null;
    }

    public String getSummary() {
        return fields != null ? fields.getSummary() : null;
    }

    public String getStoryPoints() {
        return fields != null && fields.getCustomfield_10102() != null ? fields.getCustomfield_10102().intValue() + " SP" : null;
    }

    public String getEposPoints() {
        return fields != null && fields.getCustomfield_10105() != null ? fields.getCustomfield_10105() : null;
    }

    public PrintRequest asPrintRequest() {
        String header = getKey();
        return new PrintRequest(getTemplate(), header, getStoryPoints(), getEposPoints(), getSummary());
    }
}
