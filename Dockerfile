
# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim as build

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/practice-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port (replace with your app's actual port)
EXPOSE 1712

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
