# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-slim

# Argument for the JAR file name
ARG JAR_FILE=build/libs/WebScraping-0.0.1-SNAPSHOT.jar

# Copy the JAR file to the Docker image
COPY ${JAR_FILE} app.jar

# Expose the port that the application runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
