package ar.edu.utn.frba.dds.dominio.solicitudes.procesador;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.solicitudes.baja.SolicitudBajaHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.exceptions.bajas.MotivoDeBajaMuyCortoException;
import ar.edu.utn.frba.dds.dominio.solicitudes.observers.SolicitudBajaObserver;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitud;
import java.util.ArrayList;
import java.util.List;

public class ProcesadorDeSolicitudesDeBaja extends ProcesadorDeSolicitudes<SolicitudBajaHecho> {

  private final List<SolicitudBajaObserver> solicitudBajaObservers = new ArrayList<>();

  public ProcesadorDeSolicitudesDeBaja(
      RepositorioSolicitud repoSolicitudes,
      List<SolicitudBajaObserver> solicitudBajaObservers
  ) {
    super(repoSolicitudes);
    this.solicitudBajaObservers.addAll(solicitudBajaObservers);
  }

  public SolicitudBajaHecho crearSolicitudDeBaja(
      CriterioDePertenencia criterio, String descripcion, Usuario usuario
  ) {
    if (descripcion.length() < 500) {
      throw new MotivoDeBajaMuyCortoException();
    }
    SolicitudBajaHecho solicitud = new SolicitudBajaHecho(criterio, descripcion, usuario);

    withTransaction(() -> {
      repoSolicitudes.crear(solicitud);
    });

    solicitudBajaObservers
        .forEach(observer -> observer.solicitudBajaCreada(solicitud, this));
    return solicitud;
  }
}
