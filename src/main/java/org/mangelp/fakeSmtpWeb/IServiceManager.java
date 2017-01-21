/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb;

import java.net.InetAddress;

public interface IServiceManager {
	/**
	 * Gets the port to listen for incoming connections
	 *
	 * @return
	 */
	public int getPort();

	/**
	 * Gets the address to bind the service to
	 *
	 * @return
	 */
	public InetAddress getBindAddress();

	/**
	 * Gets if the service was started
	 *
	 * @return
	 */
	public boolean isStarted();

	/**
	 * Try to start the service.
	 *
	 * If the started flag is true this method returns false. Otherwise it
	 * returns true if the service could be started or false if not.
	 *
	 * @return
	 */
	public boolean start();

	/**
	 * Try to stop the service.
	 *
	 * If the started flag is false this method returns false. Otherwise it
	 * returns true if the service could be stopped or false if not.
	 *
	 * @return
	 */
	public boolean stop();
}
