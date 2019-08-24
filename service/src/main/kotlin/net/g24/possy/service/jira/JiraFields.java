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

// TODO (https://github.com/gerald24/possy/issues/5) convert to kotlin

/**
 * @author: Gerald Leeb
 */
public class JiraFields {

    private String summary;
    /**
     * story points
     */
    private Double customfield_10102;
    private JiraIssueType issuetype;

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public Double getCustomfield_10102() {
        return customfield_10102;
    }

    public void setCustomfield_10102(final Double customfield_10102) {
        this.customfield_10102 = customfield_10102;
    }

    public JiraIssueType getIssuetype() {
        return issuetype;
    }

    public void setIssuetype(final JiraIssueType issuetype) {
        this.issuetype = issuetype;
    }

    public String getIssuetypeId() {
        return issuetype != null ? issuetype.getId() : null;
    }

    public String getIssuetypeName() {
        return issuetype != null ? issuetype.getName() : null;
    }
}
