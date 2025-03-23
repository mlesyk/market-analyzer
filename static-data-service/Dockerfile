FROM maven:3.8.6-eclipse-temurin-11-alpine AS builder
WORKDIR /opt/app
COPY pom.xml ./
RUN mvn dependency:go-offline
COPY ./src ./src
RUN mvn clean package


FROM eclipse-temurin:11-jdk-alpine
WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/target/*.jar /opt/app/static-data-service.jar
ENTRYPOINT ["java", "-jar", "/opt/app/static-data-service.jar" ]