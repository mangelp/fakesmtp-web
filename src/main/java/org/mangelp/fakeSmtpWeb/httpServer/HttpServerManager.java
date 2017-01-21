/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;

import org.mangelp.fakeSmtpWeb.AbstractServiceManager;
import org.mangelp.fakeSmtpWeb.IServiceManager;

import com.nilhcem.fakesmtp.core.exception.BindPortException;
import com.nilhcem.fakesmtp.core.exception.OutOfRangePortException;

/**
 * Starts the configured HttpServer instance and sets up all the handlers
 *
 * @author mangelp
 *
 */
public class HttpServerManager extends AbstractServiceManager implements IServiceManager {

	private Path mailFolder;
	private HttpServer httpd;
	private int startTimeout;

	public Path getMailFolder() {
		return this.mailFolder;
	}

	public void setMailFolder(Path mailFolder) {
		this.mailFolder = mailFolder;
	}

	public HttpServer getHttpd() {
		return this.httpd;
	}

	public HttpServerManager(int port, InetAddress bindAddress, Path mailFolder) {
		super(port, bindAddress, 0);
		this.setMailFolder(mailFolder);
	}

	public HttpServerManager(int port, InetAddress bindAddress, int startTimeout, Path mailFolder) {
		super(port, bindAddress, startTimeout);
		this.setMailFolder(mailFolder);
	}

	@Override
	public String toString() {
		String result = super.toString();
		result += "$" + this.getMailFolder().toString();

		return result;
	}

	@Override
	protected void startService() throws OutOfRangePortException, BindPortException, IOException {
		this.httpd = new HttpServer(this.getBindAddress(), this.getPort());
		this.httpd.start(this.startTimeout);
	}

	@Override
	protected void stopService() throws IOException {
		this.httpd.stop();
	}
}
