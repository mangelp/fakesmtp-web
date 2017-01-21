/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc.action;

public enum Actions {
	/**
	 * View the entity
	 */
	VIEW,
	/**
	 * List entities
	 */
	LIST,
	/**
	 * Download content
	 */
	DOWNLOAD,
	/**
	 * Default controller action (view or list).
	 */
	DEFAULT
}
