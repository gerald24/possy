# Installation & Configuration Guide for possy

## POS printer setup

There are different ways to integrate your POS printer in your network,
depending on which model you are using. All descripted installations are
based on [Epson TM-T20II](https://epson.com/For-Work/Printers/POS/TM-T20II-POS-Receipt-Printer/p/C31CD52062)
with wired network connection.

Connect the printer to your network and use [EpsonNet Config](https://download.epson-biz.com/modules/pos/index.php?page=single_soft&cid=6047&scat=43&pcat=3)
for Windows or Mac OS to setup the IP address of your printer. In general it's recommended
to use a fixed/static IP address either by configure it on your DHCP server or by
specifying a fixed IP in Epson NetConfig. For details please see TM-T20II's official setup guide.

![EpsonNet Config DHCP](img/epsonNet_dhcp.png)
*Let your DHCP server or router manage the IP address of the printer. The MAC address of the printer should be pinned to a fixed IP.*

![EpsonNet Config Static IP](img/epsonNet_fixedIp.png)
*Here an example of a static network config is used to integrate the printer in your network.*

Now the printer should be discoverable via network. To verify you can try to install
the printer on your workstation and print some kind of test page or POS commands directly.

## CUPS server & printer driver setup

We provide two installation options for Docker and Respberry Pi in this guide.
Of course you can install CUPS on any server, workstation, NAS or wherever you want.
Using e.g. Ubuntu (tested with 19.04) you can use the official [CUPS driver from Epson](https://www.epson-biz.com/modules/pos/index.php?page=single_soft&cid=3731).
For CUPS on Windows you can use e.g. the Docker setup.

Special thanks to [nemik](https://github.com/nemik) for providing a great open source
driver for TM-T20II we use for possy: https://github.com/nemik/epson-tm-t20-cups

### Docker

This is the easiest way to get CUPS and the driver for TM-T20II up and running as we provide
a preconfigured Docker image. The image is based on https://github.com/didrip/cups-docker.

```
docker run -d \
  -p 631:631/tcp \
  -e CUPS_USER_ADMIN=admin \
  -e CUPS_USER_PASSWORD=admin \
  --name cups-tm-t20 \
  --restart always \
  ajgassner/epson-tm-t20-cups:latest
```

Now you have a fully configured CUPS server including TM-T20II drivers running with restricted admin area access.
The Docker container needs to reach the printer over network.

### Raspberry Pi

Using [NOOBS](https://www.raspberrypi.org/downloads/noobs/) is recommended to setup your Pi.
Install and secure your Pi according to the official installation guides and connect it to the same
network as the POS printer is connected to. Then execute following commands:

```
sudo apt-get update
sudo apt-get install cups git libcups2-dev libcupsimage2-dev
cd ~
git clone https://github.com/nemik/epson-tm-t20-cups.git
cd epson-tm-t20-cups
make
cp rastertozj /usr/lib/cups/filter/
mkdir -p /usr/share/cups/model/Epson
cp tm20.ppd /usr/share/cups/model/Epson/
```

Alternatively you can watch https://www.youtube.com/watch?v=fGOcgz6z81I to setup the Pi.

## Add one (or more) POS printers to CUPS

1. Open `http://ip-host-of-cups-server:631` in a web browser
   1. Go to *Administration* and click on *Add Printer*
1. Select the TM-T20II and click on *Continue*
   1. If the printer is not listed (e.g. when using CUPS in Docker) select *LPD/LPR Host or Printer*
1. If prompted enter the printer's connection string, e.g. `lpd://192.168.1.10:515/PASSTHRU`\
   ![CUPS - TM-T20II connection](img/cups_connection.png)
1. Enter the printer's details. Enable printer sharing. **The name is used to identify the printer in possy!**\
   ![CUPS - TM-T20II name](img/cups_printer_name.png)
1. Choose `Epson TM-T20 (en)` and click *Add Printer*\
   ![CUPS - TM-T20II driver](img/cups_driver.png)
1. Choose media size `80mm x 210mm`\
   ![CUPS - TM-T20II media size](img/cups_media_size.png)
1. Go to *Cut Options* and select `feed 3mm` and click on *Set Default Options*\
   ![CUPS - TM-T20II default options](img/cups_options.png)

The Epson TM-T20II is now connected to CUPS.

## Setup and configure possy services

There are many ways to get possy up and running. Both possy-service and possy-daemon
are simple Spring Boot JAR files with embedded Tomcat server. Let's refer to
[Spring Docs - Running Apps](https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html)
for details. For a guide on how to configure Spring Boot JARs see
[Spring Docs - Externalized Config](https://docs.spring.io/spring-boot/docs/1.2.3.RELEASE/reference/html/boot-features-external-config.html).

The preferred and easiest way to run possy is Docker. We provide Docker images for both possy-service and
possy-daemon:

- [possy-service on Dockerhub](https://cloud.docker.com/u/ajgassner/repository/docker/ajgassner/possy-service)
- [possy-daemon on Dockerhub](https://cloud.docker.com/u/ajgassner/repository/docker/ajgassner/possy-daemon)

In general you can find all available possy Spring configuration properties in the default configuration files:

- [possy-service config](../service/src/main/resources/application.yml)
- [possy-daemon config](../daemon/src/main/resources/application.yml)

### possy-daemon

#### Docker

Copy or create an `application.yml` file on the Docker host to provide a
custom possy-daemon configuration.

```
sudo docker run \
    --detach \
    --restart always \
    --publish 8081:8081 \
    --name possy-daemon \
    --log-opt max-size=50m \
    --memory 1G \
    --volume /path/to/your/application.yml:/config:ro \
    ajgassner/possy-daemon:latest
```

#### Systemd service on Raspberry Pi

Make sure JRE (Java) >= version 11 is installed on the Pi. Execute follwoing commands:

```
sudo apt-get install vim wget
cd /home/pi

wget https://github.com/gerald24/possy/releases/download/<<version>>/possy-daemon-<<version>>.jar
mv possy-daemon-<<version>>.jar possy-daemon.jar
touch possy-daemon.conf && echo "JAVA_OPTS=-Xmx512M" > possy-daemon.conf

mkdir config
vi config/application.yml # this file should contain your configuration
```

Following steps are taken from [Spring Docs - Installation as a systemd Service](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-systemd-service).

Create a script named possy-daemon.service and place it in `/etc/systemd/system` directory.
The following script offers an example:

```
[Unit]
Description=possy-daemon
After=syslog.target

[Service]
User=pi
ExecStart=/home/pi/possy-daemon.jar
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

To flag the application to start automatically on system boot, use the following command:
`sudo systemctl enable possy-daemon.service`

Refer to `man systemctl` for more details.

### possy-service

It's nearly the same procedure as with possy-daemon, but the config properties
are different. You can run possy-service as standalone JAR or using Docker.

#### Docker

Example Docker run command without mapping a volume for `application.yml`,
the configuration is made via Docker environment variables:

```
sudo docker run \
    --detach \
    --restart always \
    --publish 8080:8080 \
    --name possy-service \
    --log-opt max-size=50m \
    --memory 1G \
    --env JIRA_URL="https://my-jira.domain.any/" \
    --env JIRA_USERNAME="jira-user" \
    --env JIRA_PASSWORD="jira-pw" \
    --env SPRING_SECURITY_USER_NAME="possy-user" \
    --env SPRING_SECURITY_USER_PASSWORD="possy-pw" \
    --env POSSY_ENCRYPTION_KEY="CHANGE ME TO SOME SECURE VALUE!!!" \
    --env POSSY_ADMIN_USERNAME="possy-admin" \
    --env POSSY_ADMIN_PASSWORD="possy-admin-pw" \
    ajgassner/possy-service:latest
```
