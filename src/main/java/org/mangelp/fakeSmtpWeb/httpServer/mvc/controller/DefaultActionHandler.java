/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc.controller;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.mangelp.fakeSmtpWeb.FakeSmtpWeb;
import org.mangelp.fakeSmtpWeb.httpServer.mailBrowser.MailBrowser;
import org.mangelp.fakeSmtpWeb.httpServer.mailBrowser.MailFile;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.action.ActionInput;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.action.ActionResult;

public class DefaultActionHandler extends AbstractActionHandler {
	public DefaultActionHandler() {
		super("default");
	}

	@Override
	public void prepareActionInput(ActionInput input, String[] subPath) {
		super.prepareActionInput(input, subPath);

		if (subPath == null || subPath.length == 0) {
			return;
		}

		input.setParam("id", subPath[0]);

		for (int i = 1; i < subPath.length; i++) {
			String pName = subPath[i];
			String pValue = i + 1 < subPath.length ? subPath[i + 1] : null;

			input.setParam(pName, pValue);
		}
	}

	public void doDefaultAction(ActionInput input, ActionResult result) {
		this.doListAction(input, result);
	}

	public void doViewAction(ActionInput input, ActionResult result) {
		String mailFolder = FakeSmtpWeb.getConfig().getMailFolder();
		MailBrowser mailBrowser = new MailBrowser(mailFolder);

		result.getViewContext().put("mailFolder", mailFolder);

		String id = input.getParam("id");

		if (!StringUtils.isBlank(id)) {
			MailFile mail = mailBrowser.getMail(id);
			result.getViewContext().put("mail", mail);
			result.getViewContext().put("id", id);
		}

		result.setViewName("view");
	}

	public void doListAction(ActionInput input, ActionResult result) {
		String mailFolder = FakeSmtpWeb.getConfig().getMailFolder();
		MailBrowser mailBrowser = new MailBrowser(mailFolder);

		result.getViewContext().put("dateFormat", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
		result.getViewContext().put("mailFolder", mailFolder);
		result.getViewContext().put("mails", mailBrowser.getMails());

		result.setViewName("list");
	}
}
