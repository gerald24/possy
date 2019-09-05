# possy [![Build Status](https://travis-ci.org/gerald24/possy.svg?branch=master)](https://travis-ci.org/gerald24/possy)

Possy was created based on [Boris's](https://github.com/boris779) idea to print out tasks, bugs and other issues on POS Printer (e.g. Epson TM-T20II) instead of using Post-it®. This can be used as an addition for ***physical scrum and kanban boards***.

Possy is split into 2 parts:
- possy-daemon
- possy-service

## possy-daemon

The daemon part connects to a possy-service and fetches print requests (simply text or image content). For each print request a PDF will be generated and printed via [CUPS](https://www.cups.org) to one or more connected POS Printer(s). The daemon job might run on a [Pi](https://www.raspberrypi.org).

## possy-service

The service is a Vaadin frontend, which creates print requests either by retrieving content from jira or get manually entered issues.

# Configuration

## daemon

edit/create profile 
* see `application.properties` in `daemon/src/main/resources`
* copy fonts to `daemon/src/main/resources`

## service

edit/create profile 
* see `application.yml` in `service/src/main/resources`

