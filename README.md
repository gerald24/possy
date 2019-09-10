# possy [![Build Status](https://travis-ci.org/gerald24/possy.svg?branch=master)](https://travis-ci.org/gerald24/possy) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=net.g24.possy%3Areactor&metric=alert_status)](https://sonarcloud.io/dashboard?id=net.g24.possy%3Areactor) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=net.g24.possy%3Areactor&metric=coverage)](https://sonarcloud.io/dashboard?id=net.g24.possy%3Areactor) ![daemon](docs/img//daemon-status.png) [![Heroku possy-daemon](https://heroku-badge.herokuapp.com/?app=possy-daemon)](https://possy-daemon.herokuapp.com/) ![service](docs/img//service-status.png) [![Heroku possy-service](https://heroku-badge.herokuapp.com/?app=possy-service)](https://possy-service.herokuapp.com/)

Possy was created based on [Boris's](https://github.com/boris779) idea to print out tasks, bugs and other issues on POS Printer (e.g. Epson TM-T20II) instead of using Post-it®, or print it on regular paper and use the scissor. Possy is an addition for ***physical scrum and kanban boards***.

## Overview

![Overview](docs/img//Overview.png)

## Components

- possy-daemon (print job)
- possy-service (print requests and [Planner](docs/PLANNER.md))
- [CUPS](https://www.cups.org) (print platform) - see also [Installation Guide](docs/INSTALLATION.md) for setup
- providers like [Jira](https://www.atlassian.com/software/jira), GitHub, etc. (currently only [Jira](https://www.atlassian.com/software/jira) supported)

### possy-daemon

The daemon part connects to a possy-service and fetches print requests (simply text or image content). For each print request a PDF will be generated and printed via [CUPS](https://www.cups.org) to a specific POS Printer(s). The daemon job might run on a [Pi](https://www.raspberrypi.org).

### possy-service

The service is a Vaadin frontend, which creates print requests either by retrieving content from Jira (or other Providers) or get manually entered issues.

## Issuetypes

Possy knows 4 different types of issues:
- Stories
- Tasks
- Bugs
- Freeform (like a note)

## Printers

An issuetype is mapped to a specific printer (currently up to 3 different printer):
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

## How to create a release

1. Clone the repository with git
1. Execute `mvn release:prepare release:clean` \
   Example prompt:
   ```
   [INFO] Checking dependencies and plugins for snapshots ...
   What is the release version for "possy"? (net.g24.possy:reactor) 1.0.2: : 1.0.2
   What is SCM release tag or label for "possy"? (net.g24.possy:reactor) v1.0.2-SNAPSHOT: : v1.0.2
   What is the new development version for "possy"? (net.g24.possy:reactor) 1.0.3-SNAPSHOT: : 1.0.3-SNAPSHOT
   ```
   Preparing a release goes through the following release phases:
   1. Check that there are no uncommitted changes in the sources
   1. Check that there are no SNAPSHOT dependencies
   1. Change the version in the POMs from x-SNAPSHOT to a new version (you will be prompted for the versions to use)
   1. Transform the SCM information in the POM to include the final destination of the tag
   1. Run the project tests against the modified POMs to confirm everything is in working order
   1. Commit the modified POMs
   1. Tag the code in the SCM with a version name (this will be prompted for)
   1. Bump the version in the POMs to a new value y-SNAPSHOT (these values will also be prompted for)
   1. Commit the modified POMs
1. If everything was successful in step 2 a Docker image with the provided
   version tag (e.g. v1.0.0) will get built automatically by CI/CD now
1. Optional: Go to GitHub releases page and provide a changelog
