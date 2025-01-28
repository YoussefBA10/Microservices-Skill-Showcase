# Build stage
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/gateway-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8222

ENTRYPOINT ["java", "-jar", "app.jar"]
