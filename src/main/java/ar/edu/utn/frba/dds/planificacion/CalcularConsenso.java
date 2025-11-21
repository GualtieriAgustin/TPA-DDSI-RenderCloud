package ar.edu.utn.frba.dds.planificacion;

import ar.edu.utn.frba.dds.dominio.colecciones.Coleccion;
import ar.edu.utn.frba.dds.dominio.colecciones.NavegadorDeColecciones;
import ar.edu.utn.frba.dds.dominio.consenso.ServicioDeConsenso;
import ar.edu.utn.frba.dds.dominio.fuentes.Agregador;
import ar.edu.utn.frba.dds.dominio.fuentes.Fuente;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitud;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitudesEnMemoria;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalcularConsenso {

  private static final Logger logger = LoggerFactory.getLogger(CalcularConsenso.class);

  public static void main(String[] args) {
    logger.info("Iniciando cómputo de consensos");
    try {
      RepositorioSolicitud repoSolicitudes = new RepositorioSolicitudesEnMemoria();
      NavegadorDeColecciones navegador = new NavegadorDeColecciones(repoSolicitudes);
      ServicioDeConsenso servicioDeConsenso = new ServicioDeConsenso(navegador);
      Agregador agregador = new Agregador();
      List<Fuente> fuentes = List.of(agregador);
      List<Coleccion> colecciones = Collections.emptyList();
      servicioDeConsenso.ponderarConsensoGlobal(fuentes, colecciones);
    } catch (RuntimeException e) {
      logger.error("Error al ejecutar tarea programada", e);
    }

    logger.info("Cómputo de consensos finalizado con éxito");
  }
}
