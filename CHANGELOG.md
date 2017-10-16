# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]
### Added

### Changed

## 0.2.1 - 2017-03-24
### Added
- Bash shell script to run the jar
- Added usage help

## 0.2.0 - 2017-03-24
### Added
- Raw mail file download
- Attachment download

### Changed
- Small visual tuning

## 0.1.0 - 2017-01-21
### Added
- First version
- Command line options parsing done with JewelCli.
- Command line options to change the address and port where each server listens.
- Command line option to set the folder where the mails are stored. By default they are stored into a temporal folder.
- Simple web interface that allows to see a list of emails and view them in detail.
- HTTP server built using NanoHTTPD.
- FakeSMTP is used as the Fake SMTP server (hence the name of this project).
- Simple MVC implementation that routes requests and dispatches static resources and actions.

[Unreleased]: https://github.com/mangelp/fakeSmtp-web/compare/0.2.0...HEAD