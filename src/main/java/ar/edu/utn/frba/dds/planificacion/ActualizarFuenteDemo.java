package ar.edu.utn.frba.dds.planificacion;

import ar.edu.utn.frba.dds.dominio.fuentes.proxy.demo.Conexion;
import ar.edu.utn.frba.dds.dominio.fuentes.proxy.demo.ConexionHttp;
import ar.edu.utn.frba.dds.dominio.fuentes.proxy.demo.FuenteDemo;
import ar.edu.utn.frba.dds.dominio.fuentes.proxy.demo.ObtenedorDeHechos;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ActualizarFuenteDemo {

  private static final Logger logger = LoggerFactory.getLogger(ActualizarFuenteDemo.class);

  public static void main(String[] args) {
    logger.info("Iniciando actualización de fuente demo");
    try {
      // Esto debería salir de alguna persistencia
      String url = "http://localhost:3000/fuenteDemo";
      LocalDateTime partiendoDeEstaFecha = LocalDateTime.now().minusHours(1);

      Conexion conexion = new ConexionHttp();
      ObtenedorDeHechos obtenedorDeEventos =
          new ObtenedorDeHechos(url, conexion);
      FuenteDemo fuenteDemo = new FuenteDemo(obtenedorDeEventos);

      fuenteDemo.incorporarNuevosHechos(partiendoDeEstaFecha);
    } catch (RuntimeException e) {
      logger.error("Error al ejecutar tarea programada", e);
    }

    logger.info("Actualización de fuente demo finalizada con éxito");
  }
}
