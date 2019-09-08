# Configuration Guide for possy

## possy-daemon

1. Edit or create profile, see `application.yml` in `daemon/src/main/resources`
1. Build app with Maven `mvn clean verify`
1. Run application using `mvn spring-boot:run` or directly running Application class from your IDE
1. Open `http://localhost:8081/` in browser

## possy-service

1. Edit or create profile, see `application.yml` in `daemon/src/main/resources`
1. Build app with Maven `mvn clean verify`
1. Run application using `mvn spring-boot:run` or directly running Application class from your IDE
1. Open `http://localhost:8080/` in browser
1. Login with username `possy` and password `possy`
1. If you want to run your app production mode, run `mvn spring-boot:run -Pprod`
