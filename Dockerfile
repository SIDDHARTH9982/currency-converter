FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace
COPY . .

RUN sed -i 's/\r$//' mvnw \
    && chmod +x mvnw \
    && ./mvnw -B clean package \
    && mkdir -p /app \
    && cp target/currency-converter-0.0.1-SNAPSHOT.jar /app/app.jar

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build --chown=10001:10001 /app/app.jar /app/app.jar

USER 10001:10001
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:InitialRAMPercentage=25.0", "-XX:+UseSerialGC", "-jar", "/app/app.jar"]
