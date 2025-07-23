# Use an official OpenJDK runtime as a parent image
# Using a slim version to keep the image size small
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml to leverage Docker cache
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# IMPORTANT: Grant execute permissions to the Maven wrapper
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of your application's source code
COPY src ./src

# Package the application into a JAR file
RUN ./mvnw package -DskipTests

# The final command to run the application
# It finds the generated JAR file in the target directory
CMD ["java", "-jar", "target/*.jar"]
