/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.lexicalscope.jewel.cli.Option;

public class Config {

	public static String getDefaultMailFolder() {
		// We create a different folder for each day of the year
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyDDD");

		return System.getProperty("java.io.tmpdir") + "/faketmpweb-mails-" + sdf.format(new Date());
	}

	/**
	 * Web UI port
	 */
	private int webPort = 26180;
	/**
	 * Mail server port
	 */
	private int mailPort = 26125;
	/**
	 * IP address to listen for the web UI
	 */
	private String webBindAddress = "127.0.0.1";
	/**
	 * IP address to listen for the mail server
	 */
	private String mailBindAddress = "127.0.0.1";
	/**
	 * Folder where mails will be stored in EML format
	 */
	private String mailFolder = Config.getDefaultMailFolder();
	/**
	 * Milliseconds to wait before starting the servers
	 */
	private int waitBeforeStart = 100;
	/**
	 * Show help
	 */
	private boolean help;
	/**
	 * Show version info
	 */
	private boolean version;

	public int getWebPort() {
		return this.webPort;
	}

	@Option(defaultValue = "26180")
	public void setWebPort(int webPort) {
		this.webPort = webPort;
	}

	public int getMailPort() {
		return this.mailPort;
	}

	@Option(defaultValue = "26125")
	public void setMailPort(int mailPort) {
		this.mailPort = mailPort;
	}

	public String getWebBindAddress() {
		return this.webBindAddress;
	}

	@Option(defaultValue = "127.0.0.1", longName = "webBindAddress")
	public void setWebBindAddress(String addr) {
		this.webBindAddress = addr;
	}

	public InetAddress getWebBindInetAddress() {
		InetAddress address = null;

		try {
			address = InetAddress.getByName(this.webBindAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return address;
	}

	public String getMailBindAddress() {
		return this.mailBindAddress;
	}

	@Option(defaultValue = "127.0.0.1", longName = "mailBindAddress")
	public void setMailBindAddress(String addr) throws UnknownHostException {
		this.mailBindAddress = addr;
	}

	public InetAddress getMailBindInetAddress() {
		InetAddress address = null;

		try {
			address = InetAddress.getByName(this.mailBindAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return address;
	}

	public String getMailFolder() {
		if (StringUtils.isBlank(this.mailFolder)) {
			this.mailFolder = Config.getDefaultMailFolder();
		}

		return this.mailFolder;
	}

	public Path getMailFolderPath() {
		return Paths.get(this.mailFolder);
	}

	@Option(defaultToNull = true)
	public void setMailFolder(String mailFolder) {
		this.mailFolder = mailFolder;
	}

	public int getWaitBeforeStart() {
		return this.waitBeforeStart;
	}

	@Option(defaultValue = "0")
	public void setWaitBeforeStart(int waitBeforeStart) {
		this.waitBeforeStart = waitBeforeStart;
	}

	public boolean isHelp() {
		return this.help;
	}

	@Option(helpRequest = true)
	public void setHelp(boolean help) {
		this.help = help;
	}

	public boolean isVersion() {
		return this.version;
	}

	@Option()
	public void setVersion(boolean version) {
		this.version = version;
	}

	public void dumpToLog(Logger logger) {
		List<String> options = new ArrayList<String>();

		options.add("WebAddr: " + this.webBindAddress);
		options.add("WebPort: " + this.webPort);
		options.add("MailAddr: " + this.mailBindAddress);
		options.add("MailPort: " + this.mailPort);
		options.add("MailFolder: " + this.mailFolder);
		options.add("waitBeforeStart: " + this.waitBeforeStart);

		logger.debug("Options{ " + StringUtils.join(options.toArray(new String[] {}), ", ") + "} ");
	}
}
