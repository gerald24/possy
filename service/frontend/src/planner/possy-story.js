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
import '@vaadin/vaadin-text-field/vaadin-text-area';
import '@vaadin/vaadin-icons/vaadin-icons';
import '@vaadin/vaadin-button/vaadin-button';
import './possy-task';
import '../confirm-dialog';
import Sortable from 'sortablejs';

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
			
				#task-container {
					display: inline-block;
					padding: 0 0 0 10px;
				}
			
				.add-task {
					margin-left: 10px
				}
				</style>
			
				<vaadin-text-area id="content" class="drag-movable STORY" label="Story" maxlength="300" on-change="_dataChanged"></vaadin-text-area>
				<iron-icon id="close" icon="vaadin:close-circle" on-click="_openRemoveDialog"></iron-icon>
				<iron-icon id="move" class="story-move-handle" icon="vaadin:arrows-long-v"></iron-icon>
				<div id="task-container" class="list-group"></div>
				<vaadin-button theme="icon" title="Add Task" class="add-task" on-click="addTask">
					<iron-icon icon="vaadin:plus"></iron-icon>
				</vaadin-button>

				<confirm-dialog
					id="remove-dialog"
					title="Confirm Delete"
					message="Remove Story?"
					confirm-text="Remove"
					confirm-icon="vaadin:close"
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
		this.$["remove-dialog"].addEventListener('confirm', event => this._removeMe());
		Sortable.create(this.$["task-container"], {
			handle: '.task-move-handle',
			animation: 150
		});
	}

	focus() {
		this.$.content.focus();
	}

	addTask() {
		this._addTask().focus();
	}

	asJObject() {
		return {
			detail: this.$.content.value,
			tasks: [...this.$["task-container"].children].map(task => task.asJObject())
		};
	}

	_openRemoveDialog() {
		this.$["remove-dialog"].open();
	}

	fromJObject(value) {
		this.$.content.value = value.detail;
		value.tasks.forEach(task => {
			this._addTask().fromJObject(task);
		});
	}

	_addTask() {
		const newTask = document.createElement("possy-task");
		const container = this.$["task-container"];
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
