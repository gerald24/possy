FROM openjdk:11.0.4-jre-stretch

# fix timezone
RUN ln -s /usr/share/zoneinfo/Europe/Berlin /etc/localtime

# Export env vars
ENV LC_ALL en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8
ENV SPRING_PROFILES_ACTIVE prod

ADD service-0.0.0-SNAPSHOT.jar /tmp/service-0.0.0-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/tmp/service-0.0.0-SNAPSHOT.jar"]
