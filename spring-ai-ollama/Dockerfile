# ---------- 1) Build Stage ----------
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set working directory inside container
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copy full project and build jar
COPY . .
RUN mvn clean package -DskipTests


# ---------- 2) Run Stage ----------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy jar from builder image
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

# ENV for Ollama
ENV SPRING_AI_OLLAMA_BASEURL=http://host.docker.internal:11434

# Run jar
ENTRYPOINT ["java", "-jar", "app.jar"]
