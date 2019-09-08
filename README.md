# possy [![Build Status](https://travis-ci.org/gerald24/possy.svg?branch=master)](https://travis-ci.org/gerald24/possy) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=net.g24.possy%3Areactor&metric=alert_status)](https://sonarcloud.io/dashboard?id=net.g24.possy%3Areactor) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=net.g24.possy%3Areactor&metric=coverage)](https://sonarcloud.io/dashboard?id=net.g24.possy%3Areactor) ![daemon](docs/img//daemon-status.png) [![Heroku possy-daemon](https://heroku-badge.herokuapp.com/?app=possy-daemon)](https://possy-daemon.herokuapp.com/) ![service](docs/img//service-status.png) [![Heroku possy-service](https://heroku-badge.herokuapp.com/?app=possy-service)](https://possy-service.herokuapp.com/)

Possy was created based on [Boris's](https://github.com/boris779) idea to print out tasks, bugs and other issues on POS Printer (e.g. Epson TM-T20II) instead of using Post-it®, or print it on regular paper and use the scissor. Possy is an addition for ***physical scrum and kanban boards***.

## Overview

![Overview](docs/img//Overview.png)

## Components

- possy-daemon (print job)
- possy-service (print requests and [Planner](docs/PLANNER.md))
- [CUPS](https://www.cups.org) (print platform) - see also [Installation Guide](docs/INSTALLATION.md) for setup
- providers lika [Jira](https://www.atlassian.com/software/jira), GitHub, etc. (currently only [Jira](https://www.atlassian.com/software/jira) supported)

### possy-daemon

The daemon part connects to a possy-service and fetches print requests (simply text or image content). For each print request a PDF will be generated and printed via [CUPS](https://www.cups.org) to one or more connected POS Printer(s). The daemon job might run on a [Pi](https://www.raspberrypi.org).

### possy-service

The service is a Vaadin frontend, which creates print requests either by retrieving content from Jira (or other Providers) or get manually entered issues.

## Issuetypes

Possy knows 4 different types of issues:
- Stories
- Tasks
- Bugs
- Freeform (like a note)

## Printers

The issuetypes are mapped to 3 different printer which might use different colors:
- Stories -> white
- Tasks -> white
- Bugs -> pink
- Freeform -> yellow

## Get Started

* [Installation Guide](docs/INSTALLATION.md)
* [Configuration Guide](docs/CONFIGURATION.md)
* [Docker](docs/DOCKER.md)

## Contributing
You want to contribute? Great! Thanks for being awesome!
Please see the project related [issues](https://github.com/gerald24/possy/issues)
before you start coding. Pull requests are always welcome!

### Code & Style
In short, we provide an `.editorconfig` file.
For more information please have a look at https://editorconfig.org/.
