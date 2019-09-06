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
import {setPassiveTouchGestures} from '@polymer/polymer/lib/utils/settings';
import '@vaadin/vaadin-text-field/vaadin-text-area.js';
import '@vaadin/vaadin-icons/vaadin-icons.js';
import '@vaadin/vaadin-button/vaadin-button.js';
import '@vaadin/vaadin-confirm-dialog/vaadin-confirm-dialog.js';
import Sortable from 'sortablejs';

// TODO use properties instead of asJObject/fromJObject
// TODO confirm only if not empty
// TODO fix styling

class PossyTask extends PolymerElement {
    static get template() {
        return html`
      <style>
        :host {
          position: relative;
          overflow: hidden;
        }
        #close {
          position: absolute;
          display: none;
          top: -32px;
          left: 70px;
          width: 16px;
          height: 16px;
        }
        :host(:hover) #close {
          display: block;
        }
        #move {
          position: absolute;
          top: -32px;
          left: 0px;
          width: 16px;
          height: 16px;
          cursor: move;
          cursor: -webkit-grabbing;
        }
        #content {
          width: 160px;
          height: 160px;
        }
        #content::part(label) {
           text-indent: 24px;
        }
        #content::part(input-field) {
          background: #f7f2bc;
          color: black;
          border: 1px solid #ada984;          
        }
      </style>

      <vaadin-text-area id="content" label="Task" maxlength="300" on-change="_dataChanged"></vaadin-text-area>
      <iron-icon id="close" icon="vaadin:close-circle" on-click="confirmRemoveMe"></iron-icon>
      <iron-icon id="move" class="task-move-handle" icon="vaadin:arrows-long-h"></iron-icon>
      <vaadin-confirm-dialog id="remove-dialog" cancel header="Confirm delete" confirm-text="Delete Task" confirm-theme="error primary">
       Delete task?
      </vaadin-confirm-dialog>
    `;
    }

    constructor() {
        super();
        // Resolve warning about scroll performance
        // See https://developers.google.com/web/updates/2016/06/passive-event-listeners
        setPassiveTouchGestures(true);
    }

    ready() {
        super.ready();
        this.$["remove-dialog"].addEventListener('confirm', event => this._removeMe());
    }

    focus() {
        this.$.content.focus();
    }

    confirmRemoveMe() {
        this.$["remove-dialog"].opened = true;
    }

    asJObject() {
        return {
            detail: this.$.content.value
        };
    }

    fromJObject(value) {
        this.$.content.value = value.detail;
    }

    _dataChanged() {
        this.dispatchEvent(new CustomEvent('task-changed', {detail: this}));
    }

    _removeMe() {
        this.dispatchEvent(new CustomEvent('remove-task', {detail: this}));
    }
}

customElements.define('possy-task', PossyTask);

class PossyStory extends PolymerElement {
    static get template() {
        return html`
      <style>
        :host {
          position: relative;
          overflow: hidden;
        }
        #close {
          position: absolute;
          display: none;
          top: 15px;
          left: 90px;
          width: 16px;
          height: 16px;
        }
        :host(:hover) #close {
          display: block;
        }
        #move {
          position: absolute;
          top: 15px;
          left: 10px;
          width: 16px;
          height: 16px;
          cursor: move;
          cursor: -webkit-grabbing;
        }
        #content {
          width: 160px;
          height: 160px;
        }
        #content::part(label) {
           text-indent: 24px;
        }
        #content::part(input-field) {
          background: #f4f4f4;
          color: black;
          border: 1px solid #777;
        }
        #task-container {
          display: inline-block;
          padding: 0 0 0 10px;
        }
        .add-task {
          margin-left: 10px
        }
      </style>

      <vaadin-text-area id="content" label="Story" maxlength="300" on-change="_dataChanged"></vaadin-text-area>
      <iron-icon id="close" icon="vaadin:close-circle" on-click="confirmRemoveMe"></iron-icon>
      <iron-icon id="move" class="story-move-handle" icon="vaadin:arrows-long-v"></iron-icon>
      <div id="task-container" class="list-group"></div>
      <vaadin-button class="add-task" on-click="addTask">Add Task</vaadin-button>
      <vaadin-confirm-dialog id="remove-dialog" cancel header="Confirm delete" confirm-text="Delete Story" confirm-theme="error primary">
       Delete story?
      </vaadin-confirm-dialog>
    `;
    }

    constructor() {
        super();
        // Resolve warning about scroll performance
        // See https://developers.google.com/web/updates/2016/06/passive-event-listeners
        setPassiveTouchGestures(true);
    }

    ready() {
        super.ready();
        Sortable.create(this.$["task-container"], {
            handle: '.task-move-handle',
            animation: 150
        });
        this.$["remove-dialog"].addEventListener('confirm', event => this._removeMe());
    }

    focus() {
        this.$.content.focus();
    }

    addTask() {
        this._addTask().focus();
    }

    confirmRemoveMe() {
        this.$["remove-dialog"].opened = true;
    }

    asJObject() {
        return {
            detail: this.$.content.value,
            tasks: [...this.$["task-container"].children].map(task => task.asJObject())
        };
    }

    fromJObject(value) {
        this.$.content.value = value.detail;
        value.tasks.forEach(task => {
            this._addTask().fromJObject(task);
        });
    }

    _addTask() {
        var newTask = document.createElement("possy-task");
        var container = this.$["task-container"];
        container.appendChild(newTask);

        newTask.classList.add("list-group-item");
        newTask.focus();
        newTask.addEventListener('remove-task', (event) => {
            container.removeChild(event.detail);
            this._dataChanged();
        });
        newTask.addEventListener('task-changed', (event) => {
            this._dataChanged();
        });
        return newTask;
    }

    _dataChanged() {
        this.dispatchEvent(new CustomEvent('story-changed', {detail: this}));
    }

    _removeMe() {
        this.dispatchEvent(new CustomEvent('remove-story', {detail: this}));
    }

}

customElements.define('possy-story', PossyStory);

class PossyPlanner extends PolymerElement {
    static get template() {
        return html`
      <style>
        :host {
          display: block;
        }
        .list-group-item {
          display: block;
          background: var(--lumo-contrast-10pct);
          padding: 0 10px 8px 10px;
          margin: 10px 0 10px 0;
        }
        .browser-hint {
           font-size: smaller;
           font-style: italic;
           color: var(--lumo-contrast-90pct);
           margin-left: 5px;
        }
        .db-hint {
           font-size: smaller;
           font-style: italic;
           color: var(--lumo-contrast-70pct);
           margin-left: 5px;
        }        
      </style>
      <p class="browser-hint">Please use Chrome until styling issues will be solved for Firefox and Safari.</p>
      <div id="story-container" class="list-group"></div>
      <vaadin-button class="add-story" on-click="addStory">Add Story</vaadin-button>
      <vaadin-button class="print" on-click="confirmPrint">Print</vaadin-button>
      <vaadin-button class="reset" on-click="confirmReset">Reset</vaadin-button>
      <p class="db-hint">Data will be stored in browser's DB only (local).</p>
      <vaadin-confirm-dialog id="reset-dialog" cancel header="Confirm reset" confirm-text="Reset" confirm-theme="error primary">
       Delete all stories and tasks?
      </vaadin-confirm-dialog>
      <vaadin-confirm-dialog id="print-dialog" cancel header="Confirm print" confirm-text="Print">
       Print all stories and tasks?
      </vaadin-confirm-dialog>
    `;
    }

    constructor() {
        super();
        // Resolve warning about scroll performance
        // See https://developers.google.com/web/updates/2016/06/passive-event-listeners
        setPassiveTouchGestures(true);
    }

    ready() {
        super.ready();
        Sortable.create(this.$["story-container"], {
            handle: '.story-move-handle',
            animation: 150
        });
        this._loadStories();
        this.$["reset-dialog"].addEventListener('confirm', event => this._reset());
        this.$["print-dialog"].addEventListener('confirm', event => this._print());
    }
    addStory() {
        var newStory = this._addStory()
        newStory.addTask();
        newStory.focus();
    }

    getStories() {
        return [...this.$["story-container"].children].map(story => story.asJObject());
    }

    confirmReset() {
        this.$["reset-dialog"].opened = true;
    }

    confirmPrint() {
        this.$["print-dialog"].opened = true;
    }

    _dataChanged() {
        this._saveStories(this.getStories());
    }

    _addStory() {
        var newStory = document.createElement("possy-story");
        var container = this.$["story-container"];
        container.appendChild(newStory);

        newStory.classList.add("list-group-item");
        newStory.focus();
        newStory.addEventListener('remove-story', (event) => {
            container.removeChild(event.detail);
            this._dataChanged();
        });
        newStory.addEventListener('story-changed', (event) => {
            this._dataChanged();
        });
        return newStory;
    }


    _print() {
        this.$server.print(this.getStories());
    }

    _reset() {
        this._deleteDatabase()
        var container = this.$["story-container"];
        var children = container.children;
        while (children.length > 0) {
            container.removeChild(children[0]);
        }
        this.addStory();
    }

    _showStories(stories) {
        stories.forEach(story => {
            this._addStory().fromJObject(story);
        });
    }

    _loadStories() {
        var request = this._getOrCreateDbRequest();
        if (!request) {
            this.addStory();
            return;
        }
        var self = this;
        request.onerror = (event) => {
            self.addStory();
        };
        request.onsuccess = (event) => {
            var db = request.result;
            var loadRequest = db
                .transaction(["stories"])
                .objectStore("stories")
                .get("root");
            loadRequest.onerror = (event) => {
                self.addStory();
            };
            loadRequest.onsuccess = (event) => {
                var stories = loadRequest.result;
                if (stories) {
                    self._showStories(stories);
                } else {
                    self.addStory();
                }
            };
        };
    }

    _saveStories(stories) {
        var request = this._getOrCreateDbRequest();
        if (!request) {
            return;
        }
        request.onsuccess = (event) => {
            request.result
                .transaction(["stories"], "readwrite")
                .objectStore("stories")
                .put(stories, "root");
        };
    }

    _getOrCreateDbRequest() {
        var indexedDB = this._getIndexedDB();
        if (!indexedDB) {
            return null;
        }
        var request = indexedDB.open("Planner");
        request.onupgradeneeded = function (event) {
            var db = event.target.result;
            db.createObjectStore("stories");
        };
        return request;
    }

    _deleteDatabase() {
        var indexedDB = this._getIndexedDB();
        if (indexedDB) {
            indexedDB.deleteDatabase("Planner");
        }
    }

    _getIndexedDB() {
        return window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB;
    }
}

customElements.define('possy-planner', PossyPlanner);
