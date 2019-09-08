# Installation Guide for possy

There are different ways to integrate your possy-printer in your network, depending on which model you are using. All descripted installations are based on TM-T20II with wired network connection. If you are using a raspberrypi you use your printer also in a wifi environment as descripted.

## Raspberry Pi (Wifi)
This installation method is recommended, in wifi networks and if you need your printers 24x7 available.

### Setup your Pi

Using Noobs is recommended to setup your pi. Use Wifi for your regular network connection. For the wired lan connection, it is recommended to use fixed IP-Adresses for the PI and also for the Printers.

Use EpsonNet Config for Windows or Mac to setup the IP-Adress of your printer from Epson Website for your printer
https://download.epson-biz.com/modules/pos/ 

![epson_dhcp](docs/img/epsonNet_fixedIp.png)

### Setup cups

### Add one (or more) POS-Printer to cups

### Setup possy-daemon

### Setup possy-service

### Start printing

## Wired installations

You can also connect your TM-T20II directly to your network via LAN. In this case you should use DHCP and get the IP-Adress from your printer(s). In this case use Epson Net to set automatic IP for your printer(s).

![dhcp_screenshot](docs/img//epsonNet_dhcp.png)

### Linux 

#### Setup possy-daemon

#### Stetup possy-service

### Windows 

#### Setup possy-daemon

#### Stetup possy-service



# Configuration 

see [Configuration Guide](CONFIGURATION.md)
