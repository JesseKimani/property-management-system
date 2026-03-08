# Use a base image with Java (match your Spring Boot Java version, e.g., Java 17)
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR file (assuming you build it with Maven/Gradle to target/app.jar)
# COPY target/pms-v1-0.0.1-SNAPSHOT.jar app.jar
COPY target/*.jar app.jar

# Expose the port your Spring Boot app listens on (default 8080)
EXPOSE 8081

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]






#FROM ubuntu:latest
#LABEL authors="jesse-kimani"
#
#ENTRYPOINT ["top", "-b"]
#
