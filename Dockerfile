# ============================================
# DOCKERFILE PARA CINERAMA BACKEND - RENDER
# ============================================

# Etapa 1: Construcción (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar archivos de configuración de Maven primero (para cache de dependencias)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Descargar dependencias (esto se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir el JAR (saltando tests para deploy más rápido)
RUN mvn clean package -DskipTests -B

# Etapa 2: Ejecución (Runtime)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el JAR construido
COPY --from=build /app/target/*.jar app.jar

# Puerto que expone la aplicación
EXPOSE 8080

# Variables de entorno por defecto (se sobreescriben en Render)
ENV JAVA_OPTS="-Xmx256m -Xms128m"

# Comando para ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
