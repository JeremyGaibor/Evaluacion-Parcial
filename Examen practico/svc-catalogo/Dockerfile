# =========================
# ETAPA 1: COMPILACIÓN
# =========================
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copiar primero el pom para aprovechar la caché de Docker
COPY pom.xml .

# Descargar las dependencias
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar el proyecto y generar el archivo JAR
RUN mvn clean package -DskipTests


# =========================
# ETAPA 2: EJECUCIÓN
# =========================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear un usuario sin privilegios
RUN addgroup -S spring && adduser -S spring -G spring

# Copiar únicamente el JAR generado
COPY --from=build /app/target/*.jar app.jar

# Asignar el archivo al usuario no-root
RUN chown spring:spring app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
