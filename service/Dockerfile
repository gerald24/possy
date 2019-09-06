FROM openjdk:11.0.4-jre-stretch

# Export env vars
ENV LC_ALL en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8

ADD target/*.jar /app.jar

EXPOSE 8080
EXPOSE 5005

ENTRYPOINT [
	"java",
	"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
	"-Djava.security.egd=file:/dev/./urandom",
	"-Dvaadin.productionMode=true",
	"-jar",
	"/app.jar"
]
