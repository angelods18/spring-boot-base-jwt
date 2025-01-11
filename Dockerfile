# Fase 1: Build dell'applicazione
FROM maven:3.8.7-eclipse-temurin-17 AS build

# Impostare la directory di lavoro
WORKDIR /app

# Copiare il file pom.xml e le dipendenze di Maven
COPY pom.xml .
COPY src ./src

# Eseguire il build del progetto
RUN mvn clean package -DskipTests

# Fase 2: Creazione dell'immagine per l'applicazione
FROM openjdk:17-jdk-slim

# Impostare la directory di lavoro
WORKDIR /app

# Copiare il file JAR generato dalla fase di build
COPY --from=build /app/target/*.jar app.jar

# Esporre la porta su cui Spring Boot ascolta
EXPOSE 8080

# Comando di avvio dell'applicazione
ENTRYPOINT ["java", "-jar", "app.jar"]