# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-21 AS maven-builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=maven-builder /app/target/*.jar webflix.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "webflix.jar"]