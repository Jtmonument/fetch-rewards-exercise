FROM maven:3.8.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY src /app/src

COPY pom.xml /app

RUN ["mvn", "-f", "/app/pom.xml", "clean", "package"]

FROM openjdk:17-alpine3.14

WORKDIR /app

COPY --from=build /app/target/fetch-rewards-exercise-0.0.1-SNAPSHOT.jar /app

ENTRYPOINT ["java", "-jar", "/app/fetch-rewards-exercise-0.0.1-SNAPSHOT.jar"]