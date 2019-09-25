# Configuration & Development

Requires JDK >= 11 to be installed on your machine.

## possy-daemon

1. Edit or create profile, see `application.yml` in `daemon/src/main/resources`
1. Build app with Maven `./mvnw clean verify`
1. Run application using `./mvnw spring-boot:run` or directly running Application class from your IDE
1. Open `http://localhost:8081/` in browser

## possy-service

1. Edit or create profile, see `application.yml` in `daemon/src/main/resources`
1. Build app with Maven `./mvnw clean verify`
1. Run application using `./mvnw spring-boot:run` or directly running Application class from your IDE
1. Open `http://localhost:8080/` in browser
1. Login with username `possy` and password `possy`
