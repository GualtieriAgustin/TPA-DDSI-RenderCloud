# --- Stage 1: Build ---
# Usa una imagen con Maven y JDK 17 para compilar el proyecto
FROM maven:3.9-eclipse-temurin-17 AS builder

# Establece el directorio de trabajo
WORKDIR /app

# Copia el pom.xml para descargar dependencias y aprovechar el cache de Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia el resto del código fuente
COPY src ./src

# Compila la aplicación y crea el JAR ejecutable con dependencias
RUN mvn clean package -DskipTests

# --- Stage 2: Run ---
# Usa una imagen de Java 17 ligera para ejecutar la aplicación
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copia el JAR compilado desde la etapa de construcción
COPY --from=builder /app/target/tpa-2025-24-1.0-SNAPSHOT.jar app.jar
COPY --from=builder /app/target/actualizador-fuente-demo.jar /app/actualizador-fuente-demo.jar
COPY --from=builder /app/target/calcular-consenso.jar /app/calcular-consenso.jar
COPY --from=builder /app/target/refrescar-cache-hechos.jar /app/refrescar-cache-hechos.jar
COPY --from=builder /app/target/crear-reportes.jar /app/crear-reportes.jar

# Crea el directorio para los archivos subidos, para que Javalin lo encuentre al iniciar.
RUN mkdir uploads

# Expone el puerto 9001 que usa Javalin
EXPOSE 9001

# Comando para ejecutar la aplicación cuando el contenedor inicie
CMD ["java", "-jar", "app.jar"]