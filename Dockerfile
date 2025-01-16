# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY build/libs/acendMarketing-0.0.1-SNAPSHOT.jar /app/acendMarketing-0.0.1-SNAPSHOT.jar

# Expose the application port (replace with your app's actual port)
EXPOSE 1712

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "acendMarketing-0.0.1-SNAPSHOT.jar"]
