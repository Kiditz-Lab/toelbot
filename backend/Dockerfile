FROM amazoncorretto:21

WORKDIR /app

# Copy the exact JAR file and rename it to app.jar for simplicity
COPY build/libs/toolbot-0.0.1.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
