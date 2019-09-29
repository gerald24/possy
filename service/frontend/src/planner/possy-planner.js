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
import {html, PolymerElement} from '@polymer/polymer/polymer-element';
import {setPassiveTouchGestures} from '@polymer/polymer/lib/utils/settings';
import '@vaadin/vaadin-icons/vaadin-icons';
import '@vaadin/vaadin-button/vaadin-button';
import './possy-story';
import '../confirm-dialog';
import Sortable from 'sortablejs';

// TODO use properties instead of asJObject/fromJObject
// TODO print only if not empty
// TODO fix styling

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
        
        		.db-hint {
           			font-size: smaller;
           			font-style: italic;
           			color: var(--lumo-contrast-70pct);
           			margin-left: 5px;
           		}
      		</style>
      
      		<div id="story-container" class="list-group"></div>
      		
      		<vaadin-button class="add-story" on-click="addStory">
      			<iron-icon icon="vaadin:plus" slot="prefix"></iron-icon>
      			Add Story
      		</vaadin-button>
      		
      		<vaadin-button class="print" on-click="_openPrintDialog">
      			<iron-icon icon="vaadin:print" slot="prefix"></iron-icon>
      			Print
      		</vaadin-button>
      		
      		<vaadin-button class="reset" on-click="_openResetDialog">
      			<iron-icon icon="vaadin:arrow-backward" slot="prefix"></iron-icon>
      			Reset
      		</vaadin-button>
      		
      		<p class="db-hint">Data will be stored in browser's DB only (local).</p>
      		
      		<confirm-dialog
				id="print-dialog"
				title="Confirm Print"
				message="Print all stories and tasks?"
				confirm-text="Print"
				confirm-icon="vaadin:print"
				confirm-theme="primary"
				cancel-text="Cancel"
				cancel-icon="vaadin:close"
				cancel-theme="secondary error"
			></confirm-dialog>
			
      		<confirm-dialog
				id="reset-dialog"
				title="Confirm Reset"
				message="Delete all stories and tasks?"
				confirm-text="Reset"
				confirm-icon="vaadin:arrow-backward"
				confirm-theme="primary error"
				cancel-text="Cancel"
				cancel-theme="secondary error"
			></confirm-dialog>`;
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
		const newStory = this._addStory();
		newStory.addTask();
		newStory.focus();
	}

	getStories() {
		return [...this.$["story-container"].children].map(story => story.asJObject());
	}

	_openResetDialog() {
		this.$["reset-dialog"].open();
	}

	_openPrintDialog() {
		this.$["print-dialog"].open();
	}

	_dataChanged() {
		this._saveStories(this.getStories());
	}

	_addStory() {
		const newStory = document.createElement("possy-story");
		const container = this.$["story-container"];
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
		const container = this.$["story-container"];
		const children = container.children;
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
		const request = this._getOrCreateDbRequest();
		if (!request) {
			this.addStory();
			return;
		}
		const self = this;
		request.onerror = (event) => {
			self.addStory();
		};
		request.onsuccess = (event) => {
			const db = request.result;
			const loadRequest = db
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
		const request = this._getOrCreateDbRequest();
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
		const indexedDB = this._getIndexedDB();
		if (!indexedDB) {
			return null;
		}
		const request = indexedDB.open("Planner");
		request.onupgradeneeded = function (event) {
			const db = event.target.result;
			db.createObjectStore("stories");
		};
		return request;
	}

	_deleteDatabase() {
		const indexedDB = this._getIndexedDB();
		if (indexedDB) {
			indexedDB.deleteDatabase("Planner");
		}
	}

	_getIndexedDB() {
		return window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB;
	}
}

customElements.define('possy-planner', PossyPlanner);
