/**
 * 
 */
package org.mangelp.fakeSmtpWeb.httpServer;

import org.mangelp.fakeSmtpWeb.httpServer.mvc.IMvcResult;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.MvcErrors;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.MvcResultTypes;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.resources.Resource;

/**
 * @author mangelp
 *
 */
public class AbstractResult implements IMvcResult {
	
	private MvcErrors error;
	private String errorMsg;
	private boolean success;
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
	
	public MvcResultTypes getType() {
		return this.type;
	}
	
	public void setType(MvcResultTypes resultType) {
		this.type = resultType;
	}
	
	public Resource getResource() {
		return resource;
	}
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * 
	 */
	public AbstractResult() {
	}

}
