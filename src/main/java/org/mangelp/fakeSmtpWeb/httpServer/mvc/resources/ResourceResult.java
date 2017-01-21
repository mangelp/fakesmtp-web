/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc.resources;

import java.io.InputStream;

import org.mangelp.fakeSmtpWeb.httpServer.mvc.IMvcResult;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.MvcErrors;

public class ResourceResult implements IMvcResult {

	private MvcErrors error;
	private String errorMsg;
	private boolean success;
	private String mime;
	private String fileName;

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

	public String getMime() {
		return this.mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ResourceResult() {

	}

	public ResourceResult(String fileName, String mime) {
		this();

		this.setFileName(fileName);
		this.setMime(mime);
	}

	public InputStream getAsStream() {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(this.getFileName());

		return inputStream;
	}
}
