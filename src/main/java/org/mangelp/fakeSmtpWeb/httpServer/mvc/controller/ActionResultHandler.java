/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc.controller;

import org.mangelp.fakeSmtpWeb.httpServer.mvc.action.ActionResult;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.view.ViewHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionResultHandler {

	private static final Logger logger = LoggerFactory.getLogger(ActionResultHandler.class);

	private ActionResult actionResult;

	public ActionResult getActionResult() {
		return this.actionResult;
	}

	public void setActionResult(ActionResult actionResult) {
		this.actionResult = actionResult;
	}

	public ActionResultHandler(ActionResult actionResult) {
		this.setActionResult(actionResult);

		this.init();
	}

	protected void init() {

	}

	public String render() {
		StringBuilder result = null;

		try {
			ViewHelper viewHelper = new ViewHelper();

			if (!this.actionResult.isSuccess()) {
				viewHelper.setViewName("view");
				viewHelper.setActionHandlerName("error");
			}

			result = viewHelper.render(this.actionResult);
		} catch (Exception e) {
			ActionResultHandler.logger.error("View render failed " + e);
		}

		return result.toString();
	}
}
