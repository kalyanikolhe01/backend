# Use a Gradle image for building the application
FROM gradle:7.6.1-jdk17 AS builder
WORKDIR /app

# Copy application files
COPY . .

# Set execute permissions for gradlew
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew clean build

# Use an OpenJDK runtime image for running the application
FROM openjdk:17-jdk-slim
WORKDIR /app


# Copy any JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar /app/


# Ensure the JAR file is executable
RUN chmod +x /app/acendMarketing-0.0.1-SNAPSHOT.jar

# List files to confirm JAR file is in the container
RUN ls -l /app

# Expose the port your application runs on
EXPOSE 1712

# Command to run the application
ENTRYPOINT ["java", "-Dserver.port=1712", "-jar", "/app/acendMarketing-0.0.1-SNAPSHOT.jar"]
