/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc.action;

import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD.Method;

public class ActionInput {
	private Map<String, String> params = new HashMap<String, String>();
	private Method requestMethod;
	private String path;
	private Actions action;

	public Map<String, String> getParams() {
		return this.params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public Method getRequestMethod() {
		return this.requestMethod;
	}

	public void setRequestMethod(Method method) {
		this.requestMethod = method;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Actions getAction() {
		return this.action;
	}

	public void setAction(Actions action) {
		this.action = action;
	}

	public ActionInput() {

	}

	public String getParam(String name) {
		return this.params.get(name);
	}

	public boolean hasParam(String name) {
		return this.params.get(name) != null;
	}

	public String setParam(String param, String value) {
		return this.params.put(param, value);
	}
}
