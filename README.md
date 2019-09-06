# possy [![Build Status](https://travis-ci.org/gerald24/possy.svg?branch=master)](https://travis-ci.org/gerald24/possy) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=net.g24.possy%3Areactor&metric=alert_status)](https://sonarcloud.io/dashboard?id=net.g24.possy%3Areactor) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=net.g24.possy%3Areactor&metric=coverage)](https://sonarcloud.io/dashboard?id=net.g24.possy%3Areactor)

Possy was created based on [Boris's](https://github.com/boris779) idea to print out tasks, bugs and other issues on POS Printer (e.g. Epson TM-T20II) instead of using Post-it®. This can be used as an addition for ***physical scrum and kanban boards***.

Possy is split into 2 parts:
- possy-daemon
- possy-service

## possy-daemon [![Heroku possy-daemon](https://heroku-badge.herokuapp.com/?app=possy-daemon)](https://possy-daemon.herokuapp.com/)

The daemon part connects to a possy-service and fetches print requests (simply text or image content). For each print request a PDF will be generated and printed via [CUPS](https://www.cups.org) to one or more connected POS Printer(s). The daemon job might run on a [Pi](https://www.raspberrypi.org).

### Configuration

1. Edit or create profile, see `application.yml` in `daemon/src/main/resources`
1. Build app with Maven `mvn clean verify`
1. Run application using `mvn spring-boot:run` or directly running Application class from your IDE
1. Open `http://localhost:8081/` in browser

## possy-service [![Heroku possy-service](https://heroku-badge.herokuapp.com/?app=possy-service)](https://possy-service.herokuapp.com/)

The service is a Vaadin frontend, which creates print requests either by retrieving content from Jira or get manually entered issues.

### Configuration

1. Edit or create profile, see `application.yml` in `daemon/src/main/resources`
1. Build app with Maven `mvn clean verify`
1. Run application using `mvn spring-boot:run` or directly running Application class from your IDE
1. Open `http://localhost:8080/` in browser
1. Login with username `possy` and password `possy`
1. If you want to run your app production mode, run `mvn spring-boot:run -Pprod`
