/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc;

import org.mangelp.fakeSmtpWeb.httpServer.mvc.resources.Resource;

/**
 * 
 * @author mangelp
 *
 */
public interface IMvcResult {
	
	public boolean isSuccess();

	public void setSuccess(boolean success);

	public MvcErrors getError();

	public void setError(MvcErrors error);

	public String getErrorMsg();

	public void setErrorMsg(String errorMsg);
	
	public MvcResultTypes getType();
	
	public Resource getResource();
}
