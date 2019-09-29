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
import '../confirm-dialog';

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
          			left: 0;
          			width: 16px;
          			height: 16px;
          			cursor: move;
          			cursor: -webkit-grabbing;
        		}
        
        		#content {
          			width: 160px;
          			height: 160px;
        		}
      		</style>
      
      		<vaadin-text-area id="content" class="drag-movable TASK" label="Task" maxlength="300" on-change="_dataChanged"></vaadin-text-area>
      		<iron-icon id="close" icon="vaadin:close-circle" on-click="_openRemoveDialog"></iron-icon>
      		<iron-icon id="move" class="task-move-handle" icon="vaadin:arrows-long-h"></iron-icon>

			<confirm-dialog
				id="remove-dialog"
				title="Confirm Delete"
				message="Remove Task?"
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
	}

	focus() {
		this.$.content.focus();
	}

	asJObject() {
		return {
			detail: this.$.content.value
		};
	}

	fromJObject(value) {
		this.$.content.value = value.detail;
	}

	_openRemoveDialog() {
		this.$["remove-dialog"].open();
	}

	_dataChanged() {
		this.dispatchEvent(new CustomEvent('task-changed', {detail: this}));
	}

	_removeMe() {
		this.dispatchEvent(new CustomEvent('remove-task', {detail: this}));
	}
}

customElements.define('possy-task', PossyTask);
