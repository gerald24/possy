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

# Installation

There are different ways to integrate your possy-printer in your network, depending on which model you are using. All descripted installations are based on TM-T20II with wired network connection. If you are using a raspberrypi you use your printer also in a wifi environment as descripted.

## Raspberry Pi (Wifi)
This installation method is recommended, in wifi networks and if you need your printers 24x7 available.

### Setup your Pi

Using Noobs is recommended to setup your pi. Use Wifi for your regular network connection. For the wired lan connection, it is recommended to use fixed IP-Adresses for the PI and also for the Printers.

Use EpsonNet Config for Windows or Mac to setup the IP-Adress of your printer from Epson Website for your printer
https://download.epson-biz.com/modules/pos/ 

<img src="https://github.com/gerald24/possy/blob/installation_guide/documentation/screenshots/epsonNet_fixedIp.png" width="600">

### Setup cups

### Add one (or more) POS-Printer to cups

### Setup possy-daemon

### Setup possy-service

### Start printing

## Wired installations

You can also connect your TM-T20II directly to your network via LAN. In this case you should use DHCP and get the IP-Adress from your printer(s). In this case use Epson Net to set automatic IP for your printer(s).

<img src="https://github.com/gerald24/possy/blob/installation_guide/documentation/screenshots/epsonNet_dhcp.png" width="600">

### Linux 

#### Setup possy-daemon

#### Stetup possy-service

### Windows 

#### Setup possy-daemon

#### Stetup possy-service