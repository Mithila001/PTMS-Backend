# Stage 1: Build the application using Maven
FROM maven:3.9-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project file and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the application, skipping tests to speed up the build process
RUN mvn clean package -DskipTests


# Stage 2: Create the final, smaller production image
# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:21-jre-jammy

# Set the working directory
WORKDIR /app

# Copy the executable JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# The command to run the application
# We will pass the production profile and database credentials as environment variables during runtime
ENTRYPOINT ["java", "-jar", "app.jar"]