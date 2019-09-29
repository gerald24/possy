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
import '@vaadin/vaadin-icons/vaadin-icons';
import '@vaadin/vaadin-button/vaadin-button';
import '@vaadin/vaadin-ordered-layout/vaadin-vertical-layout';
import '@vaadin/vaadin-ordered-layout/vaadin-horizontal-layout';
import '@vaadin/vaadin-dialog/vaadin-dialog';

class ConfirmDialog extends PolymerElement {

	static get template() {
		return html`<vaadin-dialog id="dialog" no-close-on-outside-click no-close-on-esc></vaadin-dialog>`;
	}

	ready() {
		super.ready();

		this.$["dialog"].renderer = root => {
			if (root.firstElementChild) {
				return;
			}

			const verticalLayout = window.document.createElement("vaadin-vertical-layout");
			verticalLayout.setAttribute("theme", "spacing");

			const title = window.document.createElement("h4");
			title.textContent = this.getAttribute("title");

			const message = window.document.createElement("div");
			message.textContent = this.getAttribute("message");

			const buttonContainer = window.document.createElement("vaadin-horizontal-layout");
			buttonContainer.setAttribute("theme", "spacing");
			buttonContainer.setAttribute("style", "align-self: flex-end;");

			const cancelButton = window.document.createElement("vaadin-button");
			cancelButton.setAttribute("theme", this.getAttribute("cancel-theme"));
			cancelButton.addEventListener("click", () => this.close());
			if (this.hasAttribute("cancel-icon")) {
				const cancelIcon = window.document.createElement("iron-icon");
				cancelIcon.setAttribute("icon", this.getAttribute("cancel-icon"));
				cancelIcon.setAttribute("slot", "prefix");
				cancelButton.appendChild(cancelIcon);
			}
			cancelButton.appendChild(window.document.createTextNode(this.getAttribute("cancel-text")));
			buttonContainer.appendChild(cancelButton);

			const confirmButton = window.document.createElement("vaadin-button");
			confirmButton.setAttribute("theme", this.getAttribute("confirm-theme"));
			confirmButton.addEventListener("click", () => this._confirm());
			if (this.hasAttribute("confirm-icon")) {
				const confirmIcon = window.document.createElement("iron-icon");
				confirmIcon.setAttribute("icon", this.getAttribute("confirm-icon"));
				confirmIcon.setAttribute("slot", "prefix");
				confirmButton.appendChild(confirmIcon);
			}
			confirmButton.appendChild(window.document.createTextNode(this.getAttribute("confirm-text")));
			buttonContainer.appendChild(confirmButton);
			this.confirmButton = confirmButton;

			verticalLayout.appendChild(title);
			verticalLayout.appendChild(message);
			verticalLayout.appendChild(buttonContainer);
			root.appendChild(verticalLayout);
		};
	}

	open() {
		this.$["dialog"].opened = true;
		this.confirmButton.focus();
	}

	close() {
		this.$["dialog"].opened = false;
	}

	_confirm() {
		this.dispatchEvent(new CustomEvent('confirm'));
		this.close();
	}
}

customElements.define('confirm-dialog', ConfirmDialog);
