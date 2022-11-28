FROM eclipse-temurin:11-jdk-alpine as build

WORKDIR app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

USER root

RUN chmod -x ./mvnw
RUN ./mvnw install -DskipTests

FROM eclipse-temurin:11-jdk-alpine
WORKDIR app
COPY --from=build app/dependencies/ ./
COPY --from=build app/spring-boot-loader/ ./
COPY --from=build app/snapshot-dependencies/ ./
COPY --from=build app/application/ ./

CMD ["./mvnw", "spring-boot:run"]