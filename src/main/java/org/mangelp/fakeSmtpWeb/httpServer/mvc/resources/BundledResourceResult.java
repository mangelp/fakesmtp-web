/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc.resources;

import java.io.InputStream;

import org.mangelp.fakeSmtpWeb.httpServer.AbstractResult;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.IMvcResult;

/**
 * Result that points to a resource to be returned to the client.
 * 
 * The resource can be either the name of a bundled resource or an input stream to read the data from.
 * 
 * @author mangelp
 *
 */
public class BundledResourceResult extends AbstractResult implements IMvcResult {

	private Resource resource;

	public Resource getResource() {
		return resource;
	}
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public BundledResourceResult() {

	}

	public BundledResourceResult(String fileName, String mime) {
		this();

		this.setResource(new Resource(mime, fileName));
	}
	
	public BundledResourceResult(InputStream inputStream, String mime) {
		this();

		this.setResource(new Resource(mime, inputStream));
	}

	public InputStream getAsStream() {
		Resource resource = this.getResource();
		
		if (resource.getInputStream() != null) {
			return resource.getInputStream();
		}
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(resource.getResourcePath());

		return in;
	}
}
