/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */
package org.mangelp.fakeSmtpWeb;

import java.io.File;
import java.lang.management.ManagementFactory;

import org.mangelp.fakeSmtpWeb.httpServer.HttpServerManager;
import org.mangelp.fakeSmtpWeb.mailServer.FakeSmtpManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import com.lexicalscope.jewel.cli.InvalidOptionSpecificationException;

/**
 * Main entry-point class
 */
public class FakeSmtpWeb {
	private static final Logger logger = LoggerFactory.getLogger(FakeSmtpWeb.class);
	/**
	 * Once servers have been started wait in a loop in the main thread sleeping
	 * for 1/3th of a second each time
	 */
	private static final int sleepTime = 333;
	public static final String VERSION = "0.2.1";

	private static Config config;
	private static boolean terminated = false;

	public static Config getConfig() {
		return FakeSmtpWeb.config;
	}

	/**
	 * Terminates the main thread loop and stops the servers if started
	 */
	public static void terminate() {
		FakeSmtpWeb.terminated = true;
	}

	/**
	 * Gets if the termination have been done
	 *
	 * @return
	 */
	public static boolean isTerminated() {
		return FakeSmtpWeb.terminated;
	}

	public static void main(String[] args) {
		try {
			FakeSmtpWeb.config = CliFactory.parseArgumentsUsingInstance(new Config(), args);
		} catch (InvalidOptionSpecificationException e) {
			System.err.println("Invalid option: " + e.getMessage());

			return;

		} catch (HelpRequestedException e) {
			System.out.println(FakeSmtpWeb.getVersionInfo() + ", " + FakeSmtpWeb.getHelpIntro());
			System.out.println("Usage: " + e.getMessage());
			System.out.println("\n" + FakeSmtpWeb.getHelpInfo() + "\n");

			return;

		} catch (ArgumentValidationException e) {
			System.err.println(e.getClass().getName() + ": Invalid arguments: " + e.getMessage());

			return;
		}

		if (FakeSmtpWeb.config.isVersion()) {
			System.out.println(FakeSmtpWeb.getVersionInfo());
			return;
		}

		String name = ManagementFactory.getRuntimeMXBean().getName();
		FakeSmtpWeb.logger.info("FakeSmtpWeb v" + FakeSmtpWeb.VERSION + " main thread started: " + name);

		FakeSmtpWeb.config.dumpToLog(FakeSmtpWeb.logger);

		if (FakeSmtpWeb.config.getWaitBeforeStart() > 0) {
			try {
				System.out.println(
						"Waiting " + FakeSmtpWeb.config.getWaitBeforeStart() + " milliseconds before starting up.");
				Thread.sleep(FakeSmtpWeb.config.getWaitBeforeStart());
			} catch (InterruptedException e) {
				System.err.println("Aborted");
				FakeSmtpWeb.logger.debug("Initial startup wait interrupted. aborting operation.", e);
				return;
			}
		}

		if (!FakeSmtpWeb.ensureMailFolderExistsAndIsWritable()) {
			FakeSmtpWeb.logger
					.error("Mail folder does not exists and cannot be created: " + FakeSmtpWeb.config.getMailFolder());
			return;
		}

		final FakeSmtpManager smtp = new FakeSmtpManager(FakeSmtpWeb.config.getMailPort(),
				FakeSmtpWeb.config.getMailBindInetAddress(), 0, FakeSmtpWeb.config.getMailFolderPath());
		final HttpServerManager httpServer = new HttpServerManager(FakeSmtpWeb.config.getWebPort(),
				FakeSmtpWeb.config.getWebBindInetAddress(), 0, FakeSmtpWeb.config.getMailFolderPath());

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				FakeSmtpWeb.logger.debug("Shutdown hook called. Stopping servers now!");

				FakeSmtpWeb.terminate();

				if (smtp.isStarted()) {
					try {
						smtp.stop();
					} catch (Throwable t) {
						FakeSmtpWeb.logger.warn("Failed to stop SMTP server before shutdown", t);
					}
				}

				if (httpServer.isStarted()) {
					try {
						httpServer.stop();
					} catch (Throwable t) {
						FakeSmtpWeb.logger.warn("Failed to stop HTTP server before shutdown", t);
					}
				}
			}
		});

		try {
			if (smtp.start()) {

				if (!httpServer.start()) {
					FakeSmtpWeb.logger.error("Failed to start NanoHTTPD-based server. Aborting ...");
					FakeSmtpWeb.terminate();
				}
			} else {
				FakeSmtpWeb.logger.error("Failed to start FakeSMTP server. Aborting ...");
			}
		} catch (Throwable t) {
			FakeSmtpWeb.logger.error("Unhandled error. Aborting ...", t);
			FakeSmtpWeb.terminate();
		}

		while (!FakeSmtpWeb.terminated && smtp.isStarted() && httpServer.isStarted()) {
			try {
				Thread.sleep(FakeSmtpWeb.sleepTime);
			} catch (InterruptedException e) {
				FakeSmtpWeb.logger.warn("Main thread sleep interrupted");
				break;
			}
		}

		if (smtp.isStarted()) {
			try {
				smtp.stop();
			} catch (Throwable t) {
				FakeSmtpWeb.logger.warn("Failed to stop SMTP server at main thread end", t);
			}
		}

		if (httpServer.isStarted()) {
			try {
				httpServer.stop();
			} catch (Throwable t) {
				FakeSmtpWeb.logger.warn("Failed to stop HTTP server at main thread end", t);
			}
		}
	}

	/**
	 * Checks that the mail folder exists, but if not it tries to create it
	 *
	 * @return
	 */
	private static boolean ensureMailFolderExistsAndIsWritable() {
		File mailFolder = new File(FakeSmtpWeb.config.getMailFolder());

		if (!mailFolder.exists() && !mailFolder.mkdirs()) {
			return false;
		}

		return true;
	}

	public static String getVersionInfo() {
		return "FakeSmtpWeb v" + FakeSmtpWeb.VERSION;
	}

	public static String getHelpIntro() {
		return "FakeSmtp integrated with HTTP Web UI";
	}

	public static String getHelpInfo() {
		return "FakeSmtpWeb integrates FakeSMTP fake mail server with an stand-alone HTTP server to provide a web interface to show sent emails in your browser.";
	}
}
