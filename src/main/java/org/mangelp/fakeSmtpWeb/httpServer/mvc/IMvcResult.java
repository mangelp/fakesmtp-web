/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc;

public interface IMvcResult {
	public boolean isSuccess();

	public void setSuccess(boolean success);

	public MvcErrors getError();

	public void setError(MvcErrors error);

	public String getErrorMsg();

	public void setErrorMsg(String errorMsg);
}
