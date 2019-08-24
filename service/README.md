# Possy Service

## build app

```mvn clean verify```

## build docker image

```$bash
cd docker
./build.sh
```

## run application

Run application using `mvn spring-boot:run` or directly running Application class from your IDE. 

Open http://localhost:8080/ in browser

## prod build / deploy

If you want to run your app locally in the production mode, run `mvn spring-boot:run -Pprod`.

