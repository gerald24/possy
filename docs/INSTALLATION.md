# Installation Guide for possy

There are different ways to integrate your possy-printer in your network,
depending on which model you are using. All descripted installations are
based on [Epson TM-T20II](https://epson.com/For-Work/Printers/POS/TM-T20II-POS-Receipt-Printer/p/C31CD52062)
with wired network connection. If you are using a Raspberry Pi you can use your printer also in a WiFi
environment as described.

## Raspberry Pi (WiFi + LAN)
This installation method is recommended in WiFi networks and if you need your printers 24x7 available.

### Setup your Pi

Using [NOOBS](https://www.raspberrypi.org/downloads/noobs/)
is recommended to setup your Pi. Use WiFi for your regular network
connection. In general it's recommended to use fixed IP addresses for the Pi and also for the printers.

Use [EpsonNet Config](https://download.epson-biz.com/modules/pos/index.php?page=single_soft&cid=6047&scat=43&pcat=3)
for Windows or Mac to setup the IP address of your printer:
https://download.epson-biz.com/modules/pos/ 

![epson_dhcp](img/epsonNet_fixedIp.png)

### Setup CUPS

### Add one (or more) POS printers to CUPS

### Setup possy-daemon

### Setup possy-service

### Start printing

## Wired installations

You can also connect your TM-T20II directly to your network via LAN. In this case you should use DHCP and get the IP-Adress from your printer(s). In this case use Epson Net to set automatic IP for your printer(s).

![dhcp_screenshot](img/epsonNet_dhcp.png)

### Linux 

#### Setup possy-daemon

#### Stetup possy-service

### Windows 

#### Setup possy-daemon

#### Stetup possy-service



# Configuration 

see [Configuration Guide](CONFIGURATION.md)
