package ar.edu.utn.frba.dds.dominio.solicitudes.procesador;

import ar.edu.utn.frba.dds.dominio.solicitudes.SolicitudHecho;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitud;
import io.github.flbulgarelli.jpa.extras.TransactionalOps;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public abstract class ProcesadorDeSolicitudes<T extends SolicitudHecho>
    implements WithSimplePersistenceUnit, TransactionalOps {

  protected final RepositorioSolicitud repoSolicitudes;

  protected ProcesadorDeSolicitudes(RepositorioSolicitud repoSolicitudes) {
    this.repoSolicitudes = repoSolicitudes;
  }

  public void aceptar(T solicitud) {
    solicitud.aceptar();
    repoSolicitudes.actualizar(solicitud);
  }

  public void rechazar(T solicitud) {
    solicitud.rechazar();
    repoSolicitudes.actualizar(solicitud);
  }
}
