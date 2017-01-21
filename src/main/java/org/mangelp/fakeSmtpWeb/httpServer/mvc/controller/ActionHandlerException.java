/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc.controller;

public class ActionHandlerException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -2050291265355027755L;

	public ActionHandlerException() {
		super();
	}

	public ActionHandlerException(String message) {
		super(message);
	}

	public ActionHandlerException(Throwable cause) {
		super(cause);
	}

	public ActionHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ActionHandlerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
