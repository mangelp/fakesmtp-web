# Readme

Integration of FakeSMTP with a simple built-in web-based interface.

# Usage

Download the sources and execute mvn clean package, then lauch the app using the jar with all the dependencies.

Basic usage information is shown using the --help option:

```bash
$ java -jar ./fakesmtp-web-0.2.1-jar-with-dependencies.jar --help
```

```bash
FakeSmtpWeb v0.2.1, FakeSmtp integrated with HTTP Web UI
Usage: The options available are:
	[--help]
	[--mailBindAddress value]
	[--mailFolder value]
	[--mailPort value]
	[--version]
	[--waitBeforeStart value]
	[--webBindAddress value]
	[--webPort value]

FakeSmtpWeb integrates FakeSMTP fake mail server with an stand-alone HTTP server to provide a web interface to show sent emails in your browser.
```

# Dependencies

* Apache commons
* Velocity
* NanoHTTPD
* FakeSMTP
* JewelCli
* JSoup
* JQuery
* Bootstrap

# Todo

* Unit testing
* Attachment download
* Refresh views using AJAX

# License

This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. 
If a copy of the MPL was not distributed with this file, You can obtain one at 
https://mozilla.org/MPL/2.0/.
