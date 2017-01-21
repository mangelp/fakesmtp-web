/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb;

import java.io.IOException;
import java.net.InetAddress;

import com.nilhcem.fakesmtp.core.exception.BindPortException;
import com.nilhcem.fakesmtp.core.exception.OutOfRangePortException;

/**
 * @author mangelp
 *
 */
public abstract class AbstractServiceManager implements IServiceManager {

	private int port;
	private InetAddress bindAddress;
	private boolean started;
	private int startTimeout;

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		if (port < 1 || port > 65535) {
			throw new IllegalArgumentException("Invalid port " + port);
		}

		this.port = port;
	}

	public InetAddress getBindAddress() {
		return this.bindAddress;
	}

	public void setBindAddress(InetAddress bindAddress) {
		if (bindAddress == null) {
			throw new IllegalArgumentException("Smtp server bind address cannnot be null");
		}

		this.bindAddress = bindAddress;
	}

	public boolean isStarted() {
		return this.started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public int getStartTimeout() {
		return this.startTimeout;
	}

	public void setStartTimeout(int startTimeout) {
		this.startTimeout = startTimeout;
	}

	public AbstractServiceManager(int port, InetAddress bindAddress, int startTimeout) {
		this.setPort(port);
		this.setBindAddress(bindAddress);
		this.setStartTimeout(startTimeout);
	}

	public boolean start() {
		if (this.isStarted()) {
			return false;
		}

		boolean started = false;

		try {
			this.startService();
			started = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.setStarted(started);

		return started;
	}

	public boolean stop() {
		if (!this.isStarted()) {
			return false;
		}

		boolean stopped = false;

		try {
			this.stopService();
			stopped = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.setStarted(!stopped);

		return stopped;
	}

	@Override
	public String toString() {
		return String.format("smtp(%s)=>%s:%d", this.isStarted() ? "active" : "inactive",
				this.getBindAddress().toString(), this.getPort());
	}

	protected abstract void startService() throws OutOfRangePortException, BindPortException, IOException;

	protected abstract void stopService() throws IOException;

}
