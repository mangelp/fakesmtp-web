/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc.action;

import java.util.HashMap;
import java.util.Map;

import org.mangelp.fakeSmtpWeb.httpServer.mvc.MvcResultTypes;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.IMvcResult;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.MvcErrors;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.controller.AbstractActionHandler;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.resources.Resource;

public class ActionResult implements IMvcResult {
	/**
	 * Whether the action was successfully executed or not.
	 */
	private boolean success = false;
	/**
	 * Action error generated.
	 *
	 * If success is true then this string must be empty.
	 */
	private MvcErrors error;
	private String errorMsg;
	/**
	 * View context (parameters passed to view)
	 *
	 * Includes the action parameters by default.
	 */
	private Map<String, Object> viewContext = null;
	private String viewName;
	private ActionInput actionInput;
	private AbstractActionHandler actionHandler; 
	private MvcResultTypes type;
	private Resource resource;

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

	public Map<String, Object> getViewContext() {
		return this.viewContext;
	}

	public void setViewContext(Map<String, Object> viewContext) {
		this.viewContext = viewContext;
	}

	public String getViewName() {
		return this.viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public ActionInput getActionInput() {
		return this.actionInput;
	}

	public void setActionInput(ActionInput actionInput) {
		this.actionInput = actionInput;
	}

	public AbstractActionHandler getActionHandler() {
		return this.actionHandler;
	}

	public void setActionHandler(AbstractActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}
	
	public MvcResultTypes getType() {
		return type;
	}
	
	public void setType(MvcResultTypes type) {
		this.type = type;
	}
	
	public Resource getResource() {
		return this.resource;
	}
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public ActionResult(String viewName, ActionInput input, AbstractActionHandler actionHandler) {
		this.setType(MvcResultTypes.VIEW);
		this.setViewName(viewName);
		this.setSuccess(true);
		this.setActionInput(input);
		this.setActionHandler(actionHandler);

		this.initContext();
	}

	protected void initContext() {
		this.viewContext = new HashMap<String, Object>();

		this.viewContext.put("viewName", this.viewName);
		this.viewContext.put("actionInput", this.actionInput);
		this.viewContext.put("actionHandler", this.actionHandler);
	}

	public void fail(MvcErrors error, String msg) {
		this.setSuccess(false);
		this.setError(error);
		this.setErrorMsg(msg);
	}

	public void fail(MvcErrors error) {
		this.fail(error, null);
	}
}
