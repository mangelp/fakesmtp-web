/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.mangelp.fakeSmtpWeb.httpServer.HttpGetRequest;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.action.ActionInput;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.action.ActionResult;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.action.Actions;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.controller.AbstractActionHandler;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.controller.ActionHandlerException;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.controller.ActionHandlers;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.controller.DefaultActionHandler;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.resources.ErrorResult;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.resources.BundledResourceResult;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.resources.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Determines the controller and action to be executed from the URL and its
 * params
 *
 * @author mangelp
 *
 */
public class Dispatcher {

	private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

	/**
	 * Paths allowed to reach controller actions
	 */
	private static final String[] mvcPaths = new String[] { 
				"/", 
				"/default", 
				"/default/default", 
				"/default/list",
				"/default/view", 
				"/default/view/[a-zA-Z0-9/=_]+", 
				"/default/download/[a-zA-Z0-9/=_\\.]+", 
			};

	public IMvcResult dispatch(HttpGetRequest request) throws ActionHandlerException {

		String uri = request.getRequestUri();

		if (Resources.isResource(uri)) {
			return this.dispatchResource(request);
		} else if (this.isMvcPath(request.getRequestUri())) {
			return this.dispathAction(request);
		} else {
			return new ErrorResult(MvcErrors.RESOURCE_NOT_FOUND, "Resource not found: " + uri);
		}
	}

	/**
	 * Gets if the path matches any of the controller paths
	 *
	 * @param uri
	 * @return
	 */
	private boolean isMvcPath(String uri) {
		uri = "/" + StringUtils.strip(uri, "/");

		for (String allowedPath : Dispatcher.mvcPaths) {
			if (StringUtils.equalsIgnoreCase(allowedPath, uri) || uri.matches(allowedPath)) {
				return true;
			}
		}

		return false;
	}

	private BundledResourceResult dispatchResource(HttpGetRequest request) {

		String uri = request.getRequestUri();

		Dispatcher.logger.debug("Dispatching resource " + uri);

		String resourcePath = Resources.getPath(uri);
		String mime = Resources.getMime(uri);

		BundledResourceResult result = new BundledResourceResult(resourcePath, mime);

		if (resourcePath == null || mime == null) {
			Dispatcher.logger.debug("Resource not found: " + uri);
			result.setSuccess(false);
			result.setError(MvcErrors.RESOURCE_NOT_FOUND);
		} else {
			Dispatcher.logger.debug("Resource found: " + resourcePath + "#" + mime);
			result.setSuccess(true);
		}

		return result;
	}

	/**
	 * @param session
	 * @return
	 * @throws ActionHandlerException
	 *             If no action handler is found for the detected action
	 */
	private ActionResult dispathAction(HttpGetRequest request) throws ActionHandlerException {

		String parts[] = StringUtils.strip(request.getRequestUri(), "/").split("/");
		String actionHandlerName = "default";
		String actionName = "default";

		if (parts.length > 1) {
			actionHandlerName = parts[0];
		}

		if (parts.length >= 2) {
			actionName = parts[1];
		}

		String[] subPath = ArrayUtils.EMPTY_STRING_ARRAY;

		if (parts.length > 2) {
			subPath = (String[]) ArrayUtils.subarray(parts, 2, parts.length);
		}

		String logPrefix = String.format("Handle %s$%s:%s", actionHandlerName, actionName,
				StringUtils.join(subPath, "/"));

		Dispatcher.logger.debug(logPrefix);

		AbstractActionHandler actionHandler = this.findActionHandler(actionHandlerName);

		if (actionHandler == null) {
			throw new ActionHandlerException("No action handler found for " + actionHandlerName);
		}

		Actions action = this.findAction(actionName, actionHandler);

		if (action == null) {
			throw new ActionHandlerException("No action found within the action handler for " + actionName);
		}

		ActionResult result = null;

		try {
			ActionInput input = this.createActionInput(request, actionHandler, subPath);
			result = actionHandler.execute(action, input);
		} catch (Throwable t) {
			throw new ActionHandlerException("Failed to execute " + logPrefix, t);
		}

		return result;
	}

	private Actions findAction(String actionName, AbstractActionHandler actionHandler) {
		Actions action = actionHandler.findAction(actionName);

		if (action == null) {
			action = Actions.DEFAULT;
		}

		return action;
	}

	public AbstractActionHandler findActionHandler(String actionHandlerName) {

		String[] nameParts = StringUtils.splitByCharacterTypeCamelCase(StringUtils.trimToEmpty(actionHandlerName));

		for (int i = 0; i < nameParts.length; i++) {
			nameParts[i] = nameParts[i].toUpperCase();
		}

		String constantName = StringUtils.join(nameParts, "_");
		ActionHandlers selecteActionHandler = null;

		for (ActionHandlers controller : ActionHandlers.values()) {
			if (StringUtils.equals(controller.name(), constantName)) {
				selecteActionHandler = controller;
				break;
			}
		}

		AbstractActionHandler instance = null;

		switch (selecteActionHandler) {
		case DEFAULT:
			instance = new DefaultActionHandler();
			break;
		default:
			throw new IllegalArgumentException("Invalid action handler " + selecteActionHandler.name());
		}

		return instance;
	}

	public ActionInput createActionInput(HttpGetRequest request, AbstractActionHandler actionHandler,
			String[] subPath) {
		ActionInput input = new ActionInput();

		input.getParams().putAll(request.getRequestParameters());

		actionHandler.prepareActionInput(input, subPath);

		return input;
	}
}
