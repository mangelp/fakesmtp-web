/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.mailServer;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;

import org.mangelp.fakeSmtpWeb.AbstractServiceManager;
import org.mangelp.fakeSmtpWeb.IServiceManager;

import com.nilhcem.fakesmtp.core.exception.BindPortException;
import com.nilhcem.fakesmtp.core.exception.OutOfRangePortException;
import com.nilhcem.fakesmtp.model.UIModel;
import com.nilhcem.fakesmtp.server.SMTPServerHandler;

public class FakeSmtpManager extends AbstractServiceManager implements IServiceManager {

	private Path mailFolder;

	public Path getMailFolder() {
		return this.mailFolder;
	}

	public void setMailFolder(Path mailFolder) {
		this.mailFolder = mailFolder;
	}

	public FakeSmtpManager(int port, InetAddress bindAddress, Path mailFolder) {
		super(port, bindAddress, 0);
		this.setMailFolder(mailFolder);
	}

	public FakeSmtpManager(int port, InetAddress bindAddress, int startTimeout, Path mailFolder) {
		super(port, bindAddress, startTimeout);
		this.setMailFolder(mailFolder);
	}

	@Override
	public String toString() {
		return super.toString() + "$" + this.getMailFolder().toString();
	}

	@Override
	protected void startService() throws OutOfRangePortException, BindPortException, IOException {
		if (this.getStartTimeout() > 0) {
			try {
				Thread.sleep(this.getStartTimeout());
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.err.println("Start timeout aborted");
				throw new IllegalStateException("Service timeout before start aborted");
			}
		}
		// This class stores the save path for later use
		UIModel.INSTANCE.setSavePath(this.getMailFolder().toString());
		SMTPServerHandler.INSTANCE.startServer(this.getPort(), this.getBindAddress());
	}

	@Override
	protected void stopService() throws IOException {
		SMTPServerHandler.INSTANCE.stopServer();
	}
}
