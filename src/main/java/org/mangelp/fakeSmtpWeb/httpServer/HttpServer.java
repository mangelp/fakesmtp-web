/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.Dispatcher;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.IMvcResult;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.action.ActionResult;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.controller.ActionResultHandler;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.resources.ResourceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

/**
 * @author mangelp
 *
 */
public class HttpServer extends NanoHTTPD {

	private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

	private int serverPort = 0;
	private String serverAddr = "";

	public HttpServer(InetAddress addr, int port) {
		super(addr != null ? addr.getHostName() : null, port);
		this.serverPort = port;
		this.serverAddr = addr != null ? addr.toString() : "";
	}

	public HttpServer(String hostName, int port) throws UnknownHostException {
		this(InetAddress.getByName(hostName), port);
	}

	public HttpServer(int port) {
		super(port);
		this.serverPort = port;
		this.serverAddr = "localhost";
	}

	private String getLogPrefix() {
		return String.format("HTTP server %s:%d", this.serverAddr, this.serverPort);
	}

	@Override
	public void start(int timeout, boolean daemon) throws IOException {

		HttpServer.logger.info(this.getLogPrefix() + " starting");

		super.start(timeout, daemon);

		HttpServer.logger.info(this.getLogPrefix() + " started");
	}

	@Override
	public void stop() {

		HttpServer.logger.info(this.getLogPrefix() + " stopping");

		super.stop();

		HttpServer.logger.info(this.getLogPrefix() + " stopped");
	}

	protected void logRequest(HttpGetRequest request) {
		StringBuilder sb = new StringBuilder();

		sb.append(request.getClientAddress() + " GET " + request.getRequestUri());

		if (!StringUtils.isBlank(request.getQueryString())) {
			sb.append("?" + request.getQueryString());
		}

		sb.append(" ");

		HttpServer.logger.debug(sb.toString());
	}

	@Override
	public Response serve(IHTTPSession session) {

		String mimeType = NanoHTTPD.MIME_HTML;

		if (session.getMethod() != Method.GET) {
			HttpServer.logger.warn("Invalid request method " + session.getMethod().name());
			return NanoHTTPD.newFixedLengthResponse(Status.BAD_REQUEST, mimeType, "");
		}

		Dispatcher dispatcher = new Dispatcher();

		IMvcResult result = null;
		HttpGetRequest getRequest = null;

		try {
			getRequest = new HttpGetRequest(session);
			this.logRequest(getRequest);
			result = dispatcher.dispatch(getRequest);
		} catch (Exception e) {
			HttpServer.logger.error("Failed to dispatch request", e);
			return NanoHTTPD.newFixedLengthResponse(Status.NOT_FOUND, mimeType, "");
		}

		Status status = Status.OK;

		if (!result.isSuccess()) {
			if (result.getError() != null) {
				switch (result.getError()) {
				case INVALID_ACTION:
					// fall through
				case INVALID_PARAMS:
					// fall through
				case INVALID_CONTROLLER:
					status = Status.BAD_REQUEST;
					break;
				case UNHANDLED_ACTION_ERROR:
				case UNHANDLED_CONTROLLER_ERROR:
				case UNHANDLED_ERROR:
					status = Status.INTERNAL_ERROR;
					break;
				case RESOURCE_NOT_FOUND:
					status = Status.NOT_FOUND;
					break;
				default:
					status = Status.NOT_IMPLEMENTED;
					break;
				}
			} else {
				status = Status.INTERNAL_ERROR;
			}

			return NanoHTTPD.newFixedLengthResponse(status, mimeType, result.getErrorMsg());
		}

		if (result instanceof ActionResult) {
			ActionResultHandler resultHandler = new ActionResultHandler((ActionResult) result);

			return NanoHTTPD.newFixedLengthResponse(status, mimeType, resultHandler.render());
		} else if (result instanceof ResourceResult) {
			ResourceResult resource = (ResourceResult) result;

			InputStream inputStream = resource.getAsStream();
			return NanoHTTPD.newFixedLengthResponse(status, resource.getMime(), inputStream, -1);
		} else {
			throw new RuntimeException("Invalid result class " + result.getClass().getSimpleName());
		}
	}
}
