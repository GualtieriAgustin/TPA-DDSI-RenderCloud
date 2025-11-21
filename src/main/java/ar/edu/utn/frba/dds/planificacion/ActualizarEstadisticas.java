package ar.edu.utn.frba.dds.planificacion;

import ar.edu.utn.frba.dds.dominio.estadisticas.EstadisticasService;
import ar.edu.utn.frba.dds.dominio.estadisticas.EstadisticasServiceImpl;
import ar.edu.utn.frba.dds.dominio.estadisticas.RepositorioEstadisticas;
import ar.edu.utn.frba.dds.dominio.estadisticas.querys.CategoriaConMasHechos;
import ar.edu.utn.frba.dds.dominio.estadisticas.querys.HoraConMasHechosPorCategoria;
import ar.edu.utn.frba.dds.dominio.estadisticas.querys.ProvinciaConMasHechos;
import ar.edu.utn.frba.dds.dominio.estadisticas.querys.ProvinciaConMasHechosPorCategoria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActualizarEstadisticas {

  private static final Logger logger = LoggerFactory.getLogger(ActualizarEstadisticas.class);

  public static void main(String[] args) {
    logger.info("Iniciando actualización de estadisticas");

    try {

      RepositorioEstadisticas repositorioEstadisticas =
          new RepositorioEstadisticas();
      EstadisticasService servicioDeEstadisticas =
          new EstadisticasServiceImpl(repositorioEstadisticas);

      var queryCategoriaConMasHechos = new CategoriaConMasHechos();
      var queryHoraConMasHechosPorCategoria = new HoraConMasHechosPorCategoria();
      var queryProvinciaConMasHechos = new ProvinciaConMasHechos();
      var queryProvinciaConMasHechosPorCategoria = new ProvinciaConMasHechosPorCategoria();

      servicioDeEstadisticas.addQuery(queryCategoriaConMasHechos);
      servicioDeEstadisticas.addQuery(queryHoraConMasHechosPorCategoria);
      servicioDeEstadisticas.addQuery(queryProvinciaConMasHechos);
      servicioDeEstadisticas.addQuery(queryProvinciaConMasHechosPorCategoria);

      servicioDeEstadisticas.generarEstadisticas(
              java.time.LocalDateTime.now(),
              java.time.LocalDateTime.now().plusDays(5)
      );
    } catch (RuntimeException e) {
      logger.error("Error al ejecutar tarea programada", e);
    }

    logger.info("Actualización de estadisticas finalizada con éxito");
  }
}
