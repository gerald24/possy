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
import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';

class PossyPrintRequestItem extends PolymerElement {

    static get template() {
        return html`
            <style>
              :host {
                display: block;
              }
              .print-request {
                border: 1px solid grey;
                padding: 2px 4px 2px 4px;
              }
              .print-request-header {
                font-weight: bold;
              }
              .print-request-content {
                font-style: italic;
                display: block;
              }
              .print-request-status {
                color: grey;
              }
            </style>
            <div class="print-request">
                <span class="print-request-header">[[header]]</span>
                <span class="print-request-content">[[content]]</span>
                <span class="print-request-status">[[template]] - [[status]]</span>
            </div>`;
    }

    static get is() {
        return 'possy-print-request-item';
    }
}

customElements.define(PossyPrintRequestItem.is, PossyPrintRequestItem);

export {PossyPrintRequestItem}

