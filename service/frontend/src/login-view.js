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
import '@vaadin/vaadin-text-field/vaadin-text-field';
import '@vaadin/vaadin-text-field/vaadin-password-field';
import '@vaadin/vaadin-button/vaadin-button';
import '@vaadin/vaadin-checkbox/vaadin-checkbox';
import '@vaadin/vaadin-icons/vaadin-icons';
import '@vaadin/vaadin-form-layout/vaadin-form-layout';
import '@polymer/iron-form/iron-form';
import './loading-spinner';

class LoginView extends PolymerElement {

	static get template() {
		return html`
            <style>
                #loginForm form {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    height: 100vh;
                }
            
                .logo {
                    width: 50%;
                }
            
                .app-name {
                    margin-top: 0;
                }
            
                .login {
                    text-align: center;
                    margin: var(--lumo-space-m);
                    padding: var(--lumo-space-m);
                    background-color: rgba(255, 255, 255, .8);
                    border-radius: var(--lumo-border-radius);
                    max-width: 400px;
                }
            
                .login:before {
                    position: absolute;
                    content: ' ';
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    z-index: -1;
                    background-image: url("images/background.jpg");
                    background-repeat: no-repeat;
                    background-size: cover;
                    overflow: hidden;
                }
            
                .try-again,
                .logged-out {
                    display: none;
                    padding: var(--lumo-space-m);
                    border-radius: var(--lumo-border-radius);
                }
            
                .try-again {
                    background-color: var(--lumo-error-color-10pct);
                    color: var(--lumo-error-color);
                }
            
                .logged-out {
                    background-color: var(--lumo-success-color-10pct);
                    color: var(--lumo-success-color);
                }
            
                .loading {
                    display: none;
                }
            </style>
            
            <iron-form id="loginForm" allow-redirect>
                <form id="nativeForm" action="login" method="post">
                    <div class="login">
                        <img class="logo" src="icons/icon-192x192.png" alt="Logo">
                        <h2 class="app-name">[[appName]] - Login</h2>
            
                        <loading-spinner id="loader" class="loading"></loading-spinner>
            
                        <vaadin-form-layout>
                            <vaadin-text-field id="username" name="username" placeholder="Username" autofocus="true" autocomplete="username" on-keydown="handleKeyDown">
                                <iron-icon icon="vaadin:user" slot="prefix"></iron-icon>
                            </vaadin-text-field>
            
                            <vaadin-password-field id="password" name="password" placeholder="Password" autocomplete="current-password" on-keydown="handleKeyDown">
                                <iron-icon icon="vaadin:key" slot="prefix"></iron-icon>
                            </vaadin-password-field>
                            
                            <vaadin-checkbox id="remember-me" name="remember-me" on-keydown="handleKeyDown">
                            	Remember me, stay logged-in forever
                            </vaadin-checkbox>
            
                            <vaadin-button id="loginButton" theme="primary" on-click="login">
                                <iron-icon icon="vaadin:sign-in" slot="prefix"></iron-icon>
                                Login
                            </vaadin-button>
                        </vaadin-form-layout>
            
                        <div id="error" class="try-again">
                        	Login failed - please check your credentials
                        </div>
                        <div id="logout" class="logged-out">You are now signed out.</div>
                    </div>
                </form>
            </iron-form>`;
	}

	static get is() {
		return 'login-view';
	}

	connectedCallback() {
		super.connectedCallback();

		if (window.location.search.includes("error")) {
			this.$.error.style.display = "block";
		}

		if (window.location.search.includes("logout")) {
			this.$.logout.style.display = "block";
		}
	}

	handleKeyDown(event) {
		if (event.key === 'Enter' || event.keyCode === 13) {
			this.login();
		}
	}

	login() {
		this.$.loader.style.display = "block";
		this.$.loginForm.submit();
	}
}

customElements.define(LoginView.is, LoginView);
