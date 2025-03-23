FROM maven:3.9.9-amazoncorretto-17 AS builder
WORKDIR /opt/app
COPY ./pom.xml ./
COPY ./static-data-service ./static-data-service
RUN mvn clean package


FROM amazoncorretto:17-alpine
WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/static-data-service/target/*.jar /opt/app/static-data-service.jar
ENTRYPOINT ["java", "-jar", "/opt/app/static-data-service.jar" ]