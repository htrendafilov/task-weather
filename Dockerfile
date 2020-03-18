# Use an official OpenJDK runtime as a parent image
FROM openjdk:8-jre-alpine

# set shell to bash
RUN apk update && apk add bash

# Set the working directory to /app
WORKDIR /app

# Copy the uber jar into the container at /app
COPY /target/weather-app.jar /app

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run jar file when the container launches
CMD ["java", "-jar", "weather-app.jar"]