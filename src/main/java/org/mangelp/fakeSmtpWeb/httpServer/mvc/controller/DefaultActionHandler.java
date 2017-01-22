/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc.controller;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.mangelp.fakeSmtpWeb.FakeSmtpWeb;
import org.mangelp.fakeSmtpWeb.httpServer.mailBrowser.MailAttachment;
import org.mangelp.fakeSmtpWeb.httpServer.mailBrowser.MailBrowser;
import org.mangelp.fakeSmtpWeb.httpServer.mailBrowser.MailFile;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.MvcErrors;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.MvcResultTypes;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.action.ActionInput;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.action.ActionResult;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultActionHandler extends AbstractActionHandler {

	private static final Logger logger = LoggerFactory.getLogger(DefaultActionHandler.class);

	public DefaultActionHandler() {
		super("default");
	}

	@Override
	public void prepareActionInput(ActionInput input, String[] subPath) {
		super.prepareActionInput(input, subPath);

		if (subPath == null || subPath.length == 0) {
			return;
		}

		// Process subpaths to extract args from it.
		// IE: /default/view/12i218/name/bleh extracts:
		// id => 12i218
		// name => bleh

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

	public void doDownloadAction(ActionInput input, ActionResult result) {
		String mailFolder = FakeSmtpWeb.getConfig().getMailFolder();
		MailBrowser mailBrowser = new MailBrowser(mailFolder);

		String id = input.getParam("id");
		String attachmentId = input.getParam("attachmentId");
		boolean isHtmlFormat = StringUtils.equalsIgnoreCase("html", input.getParam("format"));
		boolean isPlainFormat = StringUtils.equalsIgnoreCase("plain", input.getParam("format"));

		if (StringUtils.isBlank(id) && StringUtils.isBlank(attachmentId)) {
			result.setError(MvcErrors.INVALID_PARAMS);
			result.setErrorMsg("Invalid parameters");
		} else if (StringUtils.isBlank(attachmentId)) {
			MailFile mail = mailBrowser.getMail(id);
			try {
				InputStream in = null; 
				
				if (isHtmlFormat) {
					in = new ByteArrayInputStream(mail.getHtmlContent().getBytes(StandardCharsets.UTF_8));
					
					result.setResource(new Resource("text/html", in));
					result.setType(MvcResultTypes.RESOURCE);
				} else if (isPlainFormat) {
					result.setType(MvcResultTypes.RESOURCE);
					in = new ByteArrayInputStream(mail.getPlainContent().getBytes(StandardCharsets.UTF_8));
	
					result.setResource(new Resource("text/plain", in));
					result.setType(MvcResultTypes.RESOURCE);
				} else {
					result.setType(MvcResultTypes.RESOURCE);
					in = new FileInputStream(mail.getFile());
	
					result.setResource(new Resource("text/plain", in));
					result.setType(MvcResultTypes.RESOURCE);
				}
			} catch (Throwable t) {
				logger.error("Failed to open mail file " + mail.getFile(), t);
				result.setError(MvcErrors.INVALID_PARAMS);
				result.setErrorMsg("Invalid parameters");
			}
		} else {
			MailFile mail = mailBrowser.getMail(id);
			int index = Integer.parseInt(attachmentId);
			MailAttachment attachment = mail.getAttachments().get(index);

			result.setResource(new Resource(attachment.getContentType(), attachment.getInputStream()));
			result.setType(MvcResultTypes.RESOURCE);
		}
	}
}
