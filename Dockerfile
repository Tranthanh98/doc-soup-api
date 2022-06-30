FROM maven:3.8.1-amazoncorretto-11 AS MAVEN_BUILD

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
RUN mvn clean package

FROM openjdk:11.0.12-slim-buster as runner

WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/api-0.0.1-SNAPSHOT.jar /app/

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "api-0.0.1-SNAPSHOT.jar"]