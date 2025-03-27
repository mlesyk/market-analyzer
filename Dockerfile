FROM maven:3.9.9-amazoncorretto-17 AS builder
WORKDIR /opt/app
COPY ./pom.xml ./
COPY ./static-data-service ./static-data-service
COPY ./market-api-reader ./market-api-reader
RUN mvn clean package

FROM amazoncorretto:17 AS market-api-reader
WORKDIR /opt/app
COPY --from=builder /opt/app/market-api-reader/target/*.jar /opt/app/market-api-reader.jar
ENTRYPOINT ["java", "-jar", "/opt/app/market-api-reader.jar" ]

FROM amazoncorretto:17 AS static-data-service
WORKDIR /opt/app
COPY --from=builder /opt/app/static-data-service/target/*.jar /opt/app/static-data-service.jar
ENTRYPOINT ["java", "-jar", "/opt/app/static-data-service.jar" ]

FROM amazoncorretto:17 AS order-analyzer-service
WORKDIR /opt/app
COPY --from=builder /opt/app/order-analyzer-service/target/*.jar /opt/app/order-analyzer-service.jar
ENTRYPOINT ["java", "-jar", "/opt/app/order-analyzer-service.jar" ]