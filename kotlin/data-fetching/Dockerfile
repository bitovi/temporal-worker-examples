# Use the official Gradle image to build the application
FROM gradle:8.9.0-jdk21 as build

# Set the working directory inside the build stage
WORKDIR /app

# Copy the Gradle wrapper and configuration files
COPY build.gradle.kts settings.gradle.kts /app/

# Copy the Gradle wrapper files
COPY gradlew /app/gradlew
COPY gradle /app/gradle

# Copy the source code
COPY kotlin/data-fetching /app/kotlin/data-fetching

# Build the application
RUN ./gradlew build -x test --no-daemon

# Use the official OpenJDK image to run the application
FROM openjdk:21-jdk-slim

# Set the working directory inside the run stage
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/kotlin/data-fetching/build/libs/data-fetching-0.0.1-SNAPSHOT.jar /app/worker.jar

# Specify the command to run the application
CMD ["java", "-jar", "/app/worker.jar"]