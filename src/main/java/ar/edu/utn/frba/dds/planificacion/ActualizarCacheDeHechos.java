package ar.edu.utn.frba.dds.planificacion;

import ar.edu.utn.frba.dds.dominio.fuentes.Agregador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ActualizarCacheDeHechos {

  private static final Logger logger = LoggerFactory.getLogger(ActualizarCacheDeHechos.class);

  public static void main(String[] args) {
    logger.info("Iniciando actualización de hechos cacheados");
    try {
      Agregador agregador = new Agregador();
      agregador.refrescarCache();
    } catch (RuntimeException e) {
      logger.error("Error al ejecutar tarea programada", e);
    }

    logger.info("Actualización de hechos cacheados finalizada con éxito");
  }
}
