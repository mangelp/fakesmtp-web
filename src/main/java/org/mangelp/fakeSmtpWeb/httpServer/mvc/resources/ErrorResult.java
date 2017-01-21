/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc.resources;

import org.mangelp.fakeSmtpWeb.httpServer.AbstractResult;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.IMvcResult;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.MvcErrors;

public class ErrorResult extends AbstractResult implements IMvcResult {

	public ErrorResult(MvcErrors error, String errorMsg) {
		this.setError(error);
		this.setErrorMsg(errorMsg);
	}
}
