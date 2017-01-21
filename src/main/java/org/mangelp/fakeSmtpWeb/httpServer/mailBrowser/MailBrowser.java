/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */

package org.mangelp.fakeSmtpWeb.httpServer.mailBrowser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides access to stored mails
 *
 * @author mangelp
 *
 */
public class MailBrowser {

	private static final Logger logger = LoggerFactory.getLogger(MailBrowser.class);

	private String mailFolder;

	public String getMailFolder() {
		return this.mailFolder;
	}

	public void setMailFolder(String mailFolder) {
		this.mailFolder = mailFolder;
	}

	public MailBrowser(String mailFolder) {
		this.setMailFolder(mailFolder);
	}

	public List<MailFile> getMails() {
		File folder = new File(this.getMailFolder());
		ArrayList<MailFile> mails = new ArrayList<MailFile>();

		int order = 1;
		File[] files = folder.listFiles();

		if (ArrayUtils.isEmpty(files)) {
			return mails;
		}

		for (final File file : files) {
			MailFile mail = null;

			try {
				mail = new MailFile(file);
				mail.setOrder(order++);
			} catch (FileNotFoundException e) {
				MailBrowser.logger.error("Invalid mail file " + file, e);
			} catch (MessagingException e) {
				MailBrowser.logger.error("Invalid mail " + file, e);
			} catch (ParseException e) {
				MailBrowser.logger.error("Invalid mail format " + file, e);
			} catch (IOException e) {
				MailBrowser.logger.error("Invalid mail " + file, e);
			}

			if (mail != null) {
				mails.add(mail);
			}
		}

		return mails;
	}

	public MailFile getMail(String base64FileName) {
		String fileName = new String(Base64.decodeBase64(base64FileName));
		File targetFile = new File(this.getMailFolder() + "/" + fileName);

		if (!targetFile.exists()) {
			MailBrowser.logger.error("Cannot access file " + targetFile + ". Base64: " + base64FileName);
		}

		MailFile mailFile = null;

		try {
			mailFile = new MailFile(targetFile);
		} catch (FileNotFoundException e) {
			MailBrowser.logger.error("Invalid mail file " + targetFile, e);
		} catch (MessagingException e) {
			MailBrowser.logger.error("Invalid mail " + targetFile, e);
		} catch (ParseException e) {
			MailBrowser.logger.error("Invalid mail format " + targetFile, e);
		} catch (IOException e) {
			MailBrowser.logger.error("Invalid mail " + targetFile, e);
		}

		return mailFile;
	}
}
