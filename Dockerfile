# Usa una imagen oficial de Java 17
FROM eclipse-temurin:17-jdk

# Crea una carpeta para la app
WORKDIR /app

# Copia el JAR compilado a esa carpeta
COPY target/*.jar app.jar

# Expón el puerto en el que tu app corre
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
