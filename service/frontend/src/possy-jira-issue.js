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
// TODO https://github.com/gerald24/possy/issues/3 (see styling issues e.g. Incident, Story, etc.)
import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';

class PossyJiraIssue extends PolymerElement {

    static get template() {
        return html`
            <style>
                :host {
                 display: block;
                }
                .jira-issue {
                    border-radius: 8px;
                    padding: 4px 8px 4px 8px;
                    border: 4px solid #222;
                    background: #aaa;
                    color: black;
                    cursor: pointer;
                }

                .jira-issue .jira-issue-key {
                    font-weight: bold;
                    min-width: 100px;
                    display: inline-block;
                }

                .jira-issue .jira-issue-summary {
                    font-style: italic;
                    padding-left: 20px;
                }
                .jira-issue .jira-issue-type {
                    padding-left: 20px;
                    color: #222;
                }

                .jira-issue.Epos {
                    border: 4px solid #333;
                    background: #ccc;
                    color: black;
                }

                .jira-issue.Story,
                .jira-issue.Changerequest {
                    border: 4px solid #0D71B4;
                    background: #C3DCEC;
                    color: black;
                }

                .jira-issue.Aufgabe {
                    border: 4px solid #85920D;
                    background: #E1E4C3;
                    color: black;
                }

                .jira-issue.Incident,
                .jira-issue.Request,
                .jira-issue.Servicerequest,
                .jira-issue.Fehler {
                    border: 4px solid #950E4F;
                    background: #E5C3D3;
                    color: black;
                }
            </style>
            <div class\$="jira-issue [[type]]" on-click="handleClick">
                <span class="jira-issue-key">[[key]]</span>
                <span class="jira-issue-summary">[[summary]]</span>
                <span class="jira-issue-type">[[type]]</span>
            </div>`;
    }

    static get is() {
        return 'possy-jira-issue';
    }

    handleClick() {
        // handler implemented server-side
    }
}

customElements.define(PossyJiraIssue.is, PossyJiraIssue);

export {PossyJiraIssue}

