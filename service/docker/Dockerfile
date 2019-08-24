FROM openjdk:12.0.1-jdk-oraclelinux7

# fix timezone
RUN ln -s /usr/share/zoneinfo/Europe/Berlin /etc/localtime

# Export env vars
ENV LC_ALL en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8

ADD possy-service-1.0-SNAPSHOT.jar /tmp/possy-service-1.0-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/tmp/possy-service-1.0-SNAPSHOT.jar"]
