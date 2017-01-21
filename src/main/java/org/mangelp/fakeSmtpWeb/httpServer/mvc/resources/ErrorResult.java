/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc.resources;

import org.mangelp.fakeSmtpWeb.httpServer.mvc.IMvcResult;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.MvcErrors;

public class ErrorResult implements IMvcResult {

	private MvcErrors error;
	private String errorMsg;
	private boolean success;

	public boolean isSuccess() {
		return this.success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public MvcErrors getError() {
		return this.error;
	}

	public void setError(MvcErrors error) {
		this.error = error;
	}

	public String getErrorMsg() {
		return this.errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public ErrorResult(MvcErrors error, String errorMsg) {
		this.setError(error);
		this.setErrorMsg(errorMsg);
	}
}
