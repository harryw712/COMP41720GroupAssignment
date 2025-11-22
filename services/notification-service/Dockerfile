FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY services/ordering-service/pom.xml services/ordering-service/
COPY services/restaurant-service/pom.xml services/restaurant-service/
COPY services/notification-service/pom.xml services/notification-service/
COPY services /workspace/services
RUN mvn -pl services/notification-service -am package -DskipTests

FROM eclipse-temurin:17-jre
ENV JAVA_OPTS=""
WORKDIR /app
COPY --from=build /workspace/services/notification-service/target/notification-service-0.1.0-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
