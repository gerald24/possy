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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

// TODO (https://github.com/gerald24/possy/issues/5) convert to kotlin
// TODO find better solution for custom fields (e.g. story, storypoints)

/**
 * @author: Gerald Leeb
 */
@Component
public class JiraService {

    private static final String STORY_POINTS_CUSTOM_FIELD = "customfield_10102";
    private static final String EPOS_POINTS_CUSTOM_FIELD = "customfield_10105";
    private static final String GET_ISSUES = "%s/search?fields=%s&jql=%s&maxResults=%d";
    private static final String RESULT_FIELDS = "issuetype,key,summary," + STORY_POINTS_CUSTOM_FIELD + "," + EPOS_POINTS_CUSTOM_FIELD;
    private static final String RECENT_ISSUES_FOR_PROJECT_JQL = "project=%s+AND+(created>=-1w+OR+updated>=-1w)+ORDER+BY+updated+DESC";
    private static final String ENCODING = "UTF-8";

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;
    private final String jiraBaseURL;
    private final String[] projects;
    private final Pattern keyPattern;

    @Autowired
    public JiraService(
            @Value("${jira.url}") String jiraBaseURL,
            @Value("${jira.username}") String username,
            @Value("${jira.password}") String password,
            @Value("${jira.projects}") String projects) {
        this.jiraBaseURL = StringUtils.removeEnd(jiraBaseURL, "/");
        this.projects = projects.split(",");
        this.restTemplate = new RestTemplate();
        this.httpHeaders = createHeadersWithAuthentication(username, password);
        this.keyPattern = keyKeyPattern();
    }

    public String[] getProjects() {
        return projects;
    }

    public List<JiraIssue> findIssues(final String project, final String jql) {
        String projectJQL = "project = " + project + " AND ";
        Matcher matcher = keyPattern.matcher(jql);
        if (matcher.find()) {
            if (matcher.group(1) == null) {
                List<JiraIssue> issuesForKeyNr = new ArrayList<>();
                try {
                    issuesForKeyNr.addAll(findIssuesForJql(String.format("%s key=%s-%s", projectJQL, project, matcher.group(2))));
                } catch (RuntimeException e) {
                    // ignore invalid key
                }
                return issuesForKeyNr;
            }
            return findIssuesForJql(projectJQL + "key=" + jql);
        }

        return findIssuesForJql(projectJQL + jql);
    }

    private List<JiraIssue> findIssuesForJql(final String jql) {
        return extractIssues(getIssues(jql, 50)).collect(Collectors.toList());
    }

    public List<JiraIssue> loadRecentIssues(final String project) {
        return extractIssues(getIssues(String.format(RECENT_ISSUES_FOR_PROJECT_JQL, project), 20)).collect(Collectors.toList());
    }

    private ResponseEntity<JqlResult> getIssues(final String jql, final int maxResults) {
        return restTemplate.exchange(getUrlForJql(RESULT_FIELDS, jql, maxResults), HttpMethod.GET, new HttpEntity(httpHeaders), JqlResult.class);
    }

    private String getUrlForJql(final String fields, final String jql, final int maxResults) {
        return String.format(GET_ISSUES, jiraBaseURL, fields, jql, maxResults);
    }

    private Stream<JiraIssue> extractIssues(final ResponseEntity<JqlResult> response) {
        if (response.getStatusCode() != HttpStatus.OK) {
            return Stream.empty();
        }
        return response.getBody().getIssues().stream();
    }

    private HttpHeaders createHeadersWithAuthentication(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + getBase64Credentials(username, password));
        return headers;
    }

    private String getBase64Credentials(final String username, final String password) {
        try {
            return new String(Base64.getEncoder().encode((username + ":" + password).getBytes(ENCODING)));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private Pattern keyKeyPattern() {
        return Pattern.compile(String.format("^(%s)?(\\d+)$",
                                             Arrays.stream(this.projects).map(project -> project + "-").collect(Collectors.joining("|"))));
    }

}
