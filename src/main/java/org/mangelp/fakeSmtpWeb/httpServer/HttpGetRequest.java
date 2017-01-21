/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;

public class HttpGetRequest {

	private String clientAgent = "Unknown Agent/1.0";
	private String clientAddress = "0.0.0.0";
	private String host = "localhost";
	private int port = 80;
	private String requestUri = "/";
	private String queryString = "";
	private Map<String, String> requestParameters;

	public String getClientAgent() {
		return this.clientAgent;
	}

	public void setClientAgent(String clientAgent) {
		this.clientAgent = clientAgent;
	}

	public String getClientAddress() {
		return this.clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Map<String, String> getRequestParameters() {
		return this.requestParameters;
	}

	public void setRequestParameters(Map<String, String> requestParameters) {
		this.requestParameters = requestParameters;
	}

	public String getRequestUri() {
		return this.requestUri;
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}

	public String getQueryString() {
		return this.queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public HttpGetRequest() {
		this.requestParameters = new HashMap<String, String>();
	}

	public HttpGetRequest(IHTTPSession httpSession) {
		this();
		this.requestParameters.putAll(httpSession.getParms());
		this.setQueryString(httpSession.getQueryParameterString());
		this.setRequestUri(httpSession.getUri());
		this.handleHeaders(httpSession.getHeaders());
	}

	private void handleHeaders(Map<String, String> headers) {
		for (String headerKey : headers.keySet()) {
			headerKey = headerKey.toLowerCase();

			if (StringUtils.equalsIgnoreCase(headerKey, "host")) {
				this.host = headers.get(headerKey);
				int semicolonPos = this.host.indexOf(':');

				if (semicolonPos >= 0) {
					this.port = Integer.parseInt(this.host.substring(semicolonPos + 1));
					this.host = this.host.substring(0, semicolonPos);
				}
			} else if (StringUtils.equalsIgnoreCase(headerKey, "user-agent")) {
				this.clientAgent = headers.get("user-agent");
			} else if (StringUtils.equalsIgnoreCase(headerKey, "remote-addr")) {
				this.clientAddress = headers.get("remote-addr");
			}
		}
	}
}
