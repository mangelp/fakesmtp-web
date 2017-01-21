/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.HttpStatus;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.MvcErrors;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.action.ActionInput;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.action.ActionResult;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.action.Actions;

public class AbstractActionHandler {

	private String name;
	private HttpStatus status;

	public String getName() {
		return this.name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public HttpStatus getStatus() {
		return this.status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public AbstractActionHandler(String name) {
		this.setName(name);
	}

	protected String getViewName(Actions action) {
		String[] nameParts = action.name().split("_");
		String name = StringUtils.capitalize(StringUtils.join(nameParts, " "));

		return name;
	}

	protected Method getActionMethod(Actions action, ActionResult result) {
		Method method = null;

		String methodName = "do" + StringUtils.capitalize(action.name().toLowerCase()) + "Action";

		try {
			method = this.getClass().getMethod(methodName, ActionInput.class, ActionResult.class);
		} catch (NoSuchMethodException nsme) {
			result.fail(MvcErrors.INVALID_ACTION, "No method " + methodName + " found to handle " + action.name()
					+ " action on class " + this.getClass().getSimpleName());
		}

		return method;
	}

	protected ActionResult createActionResult(Actions action, ActionInput input) {
		String viewName = this.getViewName(action);
		ActionResult result = new ActionResult(viewName, input, this);

		return result;
	}

	public ActionResult execute(Actions action, ActionInput input) {
		ActionResult result = this.createActionResult(action, input);
		this.beforeAction(action, input, result);

		if (!result.isSuccess()) {
			return result;
		}

		Method method = this.getActionMethod(action, result);

		if (!result.isSuccess()) {
			return result;
		} else if (method == null) {
			result.fail(MvcErrors.INVALID_ACTION, "No valid method found to be executed for action " + action.name()
					+ " in class " + this.getClass().getSimpleName());
			return result;
		}

		this.executeAction(input, result, method);

		if (!result.isSuccess()) {
			return result;
		}

		this.afterAction(action, input, result);

		return result;
	}

	protected Object executeAction(ActionInput input, ActionResult result, Method method) {
		Object methodResult = null;

		try {
			methodResult = method.invoke(this, input, result);
		} catch (IllegalAccessException e) {
			result.fail(MvcErrors.UNHANDLED_ACTION_ERROR, e.getClass().getSimpleName() + ": " + e.getMessage());
		} catch (IllegalArgumentException e) {
			result.fail(MvcErrors.UNHANDLED_ACTION_ERROR, e.getClass().getSimpleName() + ": " + e.getMessage());
		} catch (InvocationTargetException e) {
			result.fail(MvcErrors.UNHANDLED_ACTION_ERROR, e.getClass().getSimpleName() + ": " + e.getMessage());
		}

		return methodResult;
	}

	protected void afterAction(Actions action, ActionInput input, ActionResult result) {
	}

	protected void beforeAction(Actions action, ActionInput input, ActionResult result) {
	}

	public void prepareActionInput(ActionInput input, String[] subPath) {

	}

	public Actions findAction(String actionName) {
		String[] nameParts = StringUtils.splitByCharacterTypeCamelCase(StringUtils.trimToEmpty(actionName));

		for (int i = 0; i < nameParts.length; i++) {
			nameParts[i] = nameParts[i].toUpperCase();
		}

		String constantName = StringUtils.join(nameParts, "_");
		Actions result = null;

		for (Actions action : Actions.values()) {
			if (StringUtils.equals(action.name(), constantName)) {
				result = action;
				break;
			}
		}

		return result;
	}
}
