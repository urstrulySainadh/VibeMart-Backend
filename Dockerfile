# Stage 1: Build the application using Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

# Stage 2: Create the final, lightweight image
FROM openjdk:17-slim
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/VibeMart-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application runs on
EXPOSE 9090

# Set environment variables (Optional)
ENV JAVA_OPTS=""

# The command to run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
