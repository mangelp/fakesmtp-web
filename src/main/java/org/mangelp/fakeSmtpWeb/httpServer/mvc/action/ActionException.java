/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc.action;

/**
 * @author mangelp
 *
 */
public class ActionException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -6982108797293793933L;

	/**
	 *
	 */
	public ActionException() {
	}

	/**
	 * @param message
	 */
	public ActionException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ActionException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ActionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ActionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
