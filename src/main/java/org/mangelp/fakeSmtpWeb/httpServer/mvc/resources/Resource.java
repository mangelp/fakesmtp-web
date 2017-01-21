package org.mangelp.fakeSmtpWeb.httpServer.mvc.resources;

import java.io.InputStream;

/**
 * 
 * @author mangelp
 *
 */
public class Resource {

	private String mime;
	private String resourcePath;
	private InputStream inputStream;

	/**
	 * @return the mime
	 */
	public String getMime() {
		return mime;
	}

	/**
	 * @param mime
	 *            the mime to set
	 */
	public void setMime(String mime) {
		this.mime = mime;
	}

	/**
	 * @return the resourcePath
	 */
	public String getResourcePath() {
		return resourcePath;
	}

	/**
	 * @param resourcePath
	 *            the resourcePath to set
	 */
	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	/**
	 * @return the inputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * @param inputStream
	 *            the inputStream to set
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public Resource(String mime, String path) {
		this.setMime(mime);
		this.setResourcePath(path);
	}
	
	public Resource(String mime, InputStream inputStream) {
		this.setMime(mime);
		this.setInputStream(inputStream);
	}
}
