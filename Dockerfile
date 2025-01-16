# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build directory to the container's app directory
COPY build/libs/acendMarketing-0.0.1-SNAPSHOT.jar /app/acendMarketing-0.0.1-SNAPSHOT.jar

# Expose the application port (adjust the port as needed)
EXPOSE 1712

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/acendMarketing-0.0.1-SNAPSHOT.jar"]
