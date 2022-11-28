FROM eclipse-temurin:11-jdk-alpine as build

WORKDIR app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

USER root

RUN chmod +x ./mvnw
RUN ./mvnw install -DskipTests

FROM eclipse-temurin:11-jdk-alpine
WORKDIR app

CMD ["./mvnw", "spring-boot:run"]