# Use a Gradle image for building the application
FROM gradle:7.6.1-jdk17 AS builder
WORKDIR /app

# Copy application files
COPY . .

# Set execute permissions for gradlew
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew clean build

# Debug: List files in the build directory to confirm the JAR file is there
RUN ls -l /app/build/libs/

# Use an OpenJDK runtime image for running the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/build/libs/acendMarketing-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application runs on
EXPOSE 1712

# Command to run the application
ENTRYPOINT ["java", "-Dserver.port=1712", "-jar", "app.jar"]
