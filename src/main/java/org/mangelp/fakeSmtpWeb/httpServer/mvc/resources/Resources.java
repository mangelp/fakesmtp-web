/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Resources {

	private static final Logger logger = LoggerFactory.getLogger(Resources.class);
	private static final String propertiesName = "assets/resources.properties";

	private static Resources instance = null;

	protected static Resources getInstance() {
		if (Resources.instance == null) {
			Resources.instance = new Resources();
		}

		return Resources.instance;
	}

	/**
	 * Gets the path to reach the given resource URI or null if no such resource
	 * exists
	 *
	 * @param resourceUri
	 * @return the path
	 */
	public static String getPath(String resourceUri) {
		return Resources.getInstance().getResourcePath(resourceUri);
	}

	/**
	 * Gets the mime for a given resource URI or null if no such resource exists
	 *
	 * @param resourceUri
	 * @return the mime
	 */
	public static String getMime(String resourceUri) {
		return Resources.getInstance().getResourceMime(resourceUri);
	}

	/**
	 * Gets if the given URI is a resource
	 *
	 * @param resourceUri
	 * @return boolean
	 */
	public static boolean isResource(String resourceUri) {
		return Resources.getInstance().existsResource(resourceUri);
	}

	private Properties props = new Properties();

	/**
	 * Initiallizes the properties loading them from the resource file
	 */
	protected Resources() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream resourceInputStream = classLoader.getResourceAsStream(Resources.propertiesName);

		try {
			this.props.load(resourceInputStream);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load resource mappings", e);
		}
	}

	/**
	 * Converts an uri into a key
	 *
	 * @param uri
	 * @return
	 */
	protected String uriToKey(String uri) {
		String key = StringUtils.strip(uri, "/");
		key = key.replace('.', '_').replace('/', '.');

		return key;
	}

	/**
	 * Gets if the given URI matches a resource
	 *
	 * @param resourceUri
	 * @return
	 */
	public boolean existsResource(String resourceUri) {
		String key = this.uriToKey(resourceUri);

		boolean exists = this.props.containsKey(key);

		Resources.logger.debug("Exists resource " + resourceUri + "(" + key + "): ", Boolean.toString(exists));

		return exists;
	}

	/**
	 * Gets the path for a resource given its URI
	 *
	 * @param propKey
	 * @return
	 */
	protected String getResourcePath(String resourceUri) {
		String key = this.uriToKey(resourceUri);
		String result = null;

		if (this.props.containsKey(key)) {
			result = this.props.getProperty(key);
		}

		Resources.logger.debug("Path for resource " + resourceUri + " (" + key + ") is " + result);

		return result;
	}

	/**
	 * Gets the mime for a resource given its URI
	 *
	 * @param resourceUri
	 * @return
	 */
	protected String getResourceMime(String resourceUri) {
		String key = this.uriToKey(resourceUri) + "__mime";
		String result = null;

		if (this.props.containsKey(key)) {
			result = this.props.getProperty(key);
		}

		Resources.logger.debug("Mime for resource " + resourceUri + " (" + key + ") is " + result);

		return result;
	}
}
