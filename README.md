# possy

Possy was created based on [Boris's](https://github.com/boris779) idea to print out tasks, bugs and other issues on POS Printer (e.g. Epson TM-T20II) instead of using Post-it®. This can be used as an addition for ***physical scrum and kanban boards***.

Possy is split into 2 parts:
- possy-daemon
- possy-service

## possy-daemon

The daemon part connects to a possy-service and fetches print requests (simply text or image content). For each print request a PDF will be generated and printed via [CUPS](https://www.cups.org) to one or more connected POS Printer(s). The daemon job might run on a [Pi](https://www.raspberrypi.org).

## possy-service

The service is a vaadin frontend, which creates print requests either by retrieving content from jira or get manually entered issues.

# Configuration

## daemon

edit/create profile 
* see application.properties in daemon/src/main/resources
* copy fonts to daemon/src/main/resources

## service

edit/create profile 
* see application.properties in service/src/main/resources


# Installation

There are different ways to integrate your possy-printer in your network, depending on which model you are using. All descripted installations are based on TM-T20II with wired network connection. If you are using a raspberrypi you use your printer also in a wifi environment as descripted.

## Raspberry Pi (Wifi)
This installation method is recommended, in wifi networks and if you need your printers 24x7 available.

### Setup your Pi

### Setup cups

### Add one (or more) POS-Printer to cups

### Setup possy-daemon

### Setup possy-service

### Start printing

## Wired installations

You can also connect your TM-T20II directly to your network via LAN. In this case you should use DHCP and get the IP-Adress from your printer(s). 

### Linux 

#### Setup possy-daemon

#### Stetup possy-service

### Windows 

#### Setup possy-daemon

#### Stetup possy-service
