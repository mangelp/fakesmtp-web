/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb.httpServer.mvc.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.mangelp.fakeSmtpWeb.httpServer.mvc.action.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewHelper {

	private static final Logger logger = LoggerFactory.getLogger(ViewHelper.class);

	private VelocityEngine velocityEngine;
	private String viewName = null;
	private String actionHandlerName = null;

	public VelocityEngine getVelocityEngine() {
		return this.velocityEngine;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	public String getViewName() {
		return this.viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getActionHandlerName() {
		return this.actionHandlerName;
	}

	public void setActionHandlerName(String actionHandlerName) {
		this.actionHandlerName = actionHandlerName;
	}

	public ViewHelper() throws Exception {
		this.init();
	}

	protected void init() throws Exception {
		this.velocityEngine = new VelocityEngine();
		Velocity.init();

		this.velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		this.velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
	}

	protected String getTemplatePath(ActionResult actionResult) {
		String actionHandlerName = !StringUtils.isBlank(this.getActionHandlerName()) ? this.getActionHandlerName()
				: actionResult.getActionHandler().getName();
		String viewName = !StringUtils.isBlank(this.getViewName()) ? this.getViewName() : actionResult.getViewName();

		String templateRelativePath = "views/" + actionHandlerName + "/" + viewName + ".vm";

		return templateRelativePath;
	}

	protected Template getTemplate(String templateRelativePath)
			throws ResourceNotFoundException, ParseErrorException, Exception {
		Template template = this.velocityEngine.getTemplate(templateRelativePath, "UTF-8");

		return template;
	}

	protected Context createContext(ActionResult actionResult) {
		Context context = new VelocityContext(actionResult.getViewContext());

		return context;
	}

	protected StringBuilder renderTemplate(String templatePath, Context context) throws IOException, VelocityException {

		ViewHelper.logger.debug("Render template " + templatePath);
		StringBuilder builder = new StringBuilder(4096);

		InputStream input = this.getClass().getClassLoader().getResourceAsStream(templatePath);

		if (input == null) {
			throw new IOException("Template file doesn't exist: " + templatePath);
		}

		InputStreamReader templateStreamReader = null;
		StringBuilderWriter builderWriter = null;

		try {

			templateStreamReader = new InputStreamReader(input);
			builderWriter = new StringBuilderWriter(builder);

			if (!this.velocityEngine.evaluate(context, builderWriter, this.getClass().getSimpleName(),
					templateStreamReader)) {
				throw new VelocityException("Cannot parse template " + templatePath);
			}

		} catch (Throwable t) {
			throw new VelocityException("Cannot process template at path: " + templatePath, t);
		} finally {

			if (builderWriter != null) {
				builderWriter.close();
			}

			if (templateStreamReader != null) {
				templateStreamReader.close();
			}
		}

		return builder;
	}

	public StringBuilder render(ActionResult actionResult) throws Exception {
		String templatePath = this.getTemplatePath(actionResult);

		Context context = this.createContext(actionResult);

		StringBuilder builder = this.renderTemplate(templatePath, context);

		return builder;
	}
}
