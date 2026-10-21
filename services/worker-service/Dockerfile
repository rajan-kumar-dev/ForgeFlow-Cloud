FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./pom.xml
COPY services ./services
RUN mvn -B -ntp -pl services/worker-service -am -DskipTests package
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN mkdir -p /data/reports && chown -R 10001:10001 /app /data
COPY --from=build /workspace/services/worker-service/target/worker-service-1.0.0.jar /app/app.jar
USER 10001:10001
EXPOSE 8082
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-jar","/app/app.jar"]
