package ar.edu.utn.frba.dds.server;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class App {

  private static final Logger logger = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) {
    loadConfig();
    new Server().start();
  }

  private static void loadConfig() {
    String environment = System.getenv("ENV");
    if (environment == null) {
      environment = "dev";
    }
    logger.info("Cargando configuración para el entorno: {}", environment);

    Yaml yaml = new Yaml();
    try {
      InputStream inputStream = App.class
          .getClassLoader()
          .getResourceAsStream("application.yml");

      if (inputStream == null) {
        logger.warn("No se encontró el archivo application.yml.");
        return;
      }

      Map<String, Map<String, Object>> config = yaml.load(inputStream);

      // 2. Cargar propiedades por defecto y luego las específicas del entorno
      Optional.ofNullable(config.get("default")).ifPresent(defaultProps ->
          defaultProps.forEach((key, value) -> System.setProperty(key, value.toString()))
      );
      config.getOrDefault(environment, Map.of()).forEach((key, value) -> {
        String stringValue = value.toString();
        System.setProperty(key, stringValue);
      });

      logger.info("Propiedades del sistema cargadas desde application.yml");


      String dbUrlSecret = System.getenv("db_url");
      if (dbUrlSecret != null && !dbUrlSecret.isBlank()) {
        logger.info("Sobrescribiendo 'hibernate.connection.url' con secreto de entorno.");
        System.setProperty("hibernate.connection.url", dbUrlSecret);
      }

      String dbUsernameSecret = System.getenv("db_username");
      if (dbUsernameSecret != null && !dbUsernameSecret.isBlank()) {
        logger.info("Sobrescribiendo 'hibernate.connection.username' con secreto de entorno.");
        System.setProperty("hibernate.connection.username", dbUsernameSecret);
      }

      String dbPasswordSecret = System.getenv("db_password");
      if (dbPasswordSecret != null && !dbPasswordSecret.isBlank()) {
        logger.info("Sobrescribiendo 'hibernate.connection.password' con secreto de entorno.");
        System.setProperty("hibernate.connection.password", dbPasswordSecret);
      }

    } catch (RuntimeException e) {
      throw new RuntimeException("Error al cargar el archivo de configuración application.yml", e);
    }
  }
}