# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 AS maven-builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=maven-builder /app/target/*.jar webflix.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "webflix.jar"]