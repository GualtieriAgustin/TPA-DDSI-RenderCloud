package ar.edu.utn.frba.dds.servicio;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroProvincia;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroTextoLibre;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.solicitudes.SolicitudHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.procesador.ProcesadorDeSolicitudesDeBaja;
import ar.edu.utn.frba.dds.dominio.solicitudes.procesador.ProcesadorDeSolicitudesDeModificacion;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeUsuarios;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitud;
import io.github.flbulgarelli.jpa.extras.TransactionalOps;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolicitudesService implements WithSimplePersistenceUnit, TransactionalOps {

  private static final Logger logger = LoggerFactory.getLogger(SolicitudesService.class);

  private final RepositorioSolicitud repositorioSolicitud;
  private final RepositorioDeUsuarios repositorioDeUsuarios;
  private final ProcesadorDeSolicitudesDeBaja procesadorDeSolicitudesDeBaja;
  private final ProcesadorDeSolicitudesDeModificacion procesadorDeSolicitudesDeModificacion;

  public SolicitudesService(
      RepositorioSolicitud repositorioSolicitud,
      RepositorioDeUsuarios repositorioDeUsuarios,
      ProcesadorDeSolicitudesDeBaja procesadorDeSolicitudesDeBaja,
      ProcesadorDeSolicitudesDeModificacion procesadorDeSolicitudesDeModificacion) {
    this.repositorioSolicitud = repositorioSolicitud;
    this.repositorioDeUsuarios = repositorioDeUsuarios;
    this.procesadorDeSolicitudesDeBaja = procesadorDeSolicitudesDeBaja;
    this.procesadorDeSolicitudesDeModificacion = procesadorDeSolicitudesDeModificacion;
  }

  public List<SolicitudHecho> consultarTodas() {
    return repositorioSolicitud.consultarTodas();
  }

  public List<SolicitudHecho> consultarPendientes() {
    return repositorioSolicitud.consultarPendientes();
  }

  public void crearSolicitudDeBaja(String titulo, String descripcionHecho,
                                   Provincia provincia, String justificacion, String username) {
    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Debe iniciar sesión para crear una solicitud");
    }
    Usuario usuario = repositorioDeUsuarios.buscarUsuario(username);
    if (usuario == null) {
      throw new IllegalArgumentException("El usuario es inexistente");
    }

    CriterioDePertenencia criterio = new CriterioDePertenencia(
        List.of(
            new FiltroTextoLibre(titulo + " " + descripcionHecho),
            new FiltroProvincia(provincia)
        )
    );

    procesadorDeSolicitudesDeBaja.crearSolicitudDeBaja(criterio, justificacion, usuario);
    logger.info("Solicitud de baja creada con éxito por el usuario {}", username);
  }

  public void aprobarSolicitud(Long solicitudId) {
    withTransaction(() -> {
      SolicitudHecho solicitud = repositorioSolicitud.consultarPorId(solicitudId);
      if (solicitud == null) {
        throw new RuntimeException("No se encontró la solicitud con ID: " + solicitudId);
      }
      solicitud.aceptar();
      logger.info("Solicitud {} aprobada.", solicitudId);
    });
  }

  public void rechazarSolicitud(Long solicitudId) {
    withTransaction(() -> {
      SolicitudHecho solicitud = repositorioSolicitud.consultarPorId(solicitudId);
      if (solicitud == null) {
        throw new RuntimeException("No se encontró la solicitud con ID: " + solicitudId);
      }
      solicitud.rechazar();
      logger.info("Solicitud {} rechazada.", solicitudId);
    });
  }
}