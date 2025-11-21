package ar.edu.utn.frba.dds.dominio.solicitudes.procesador;

import ar.edu.utn.frba.dds.dominio.solicitudes.exceptions.modificaciones.NoPuedeModificarHechoFueraDePlazoModificacionException;
import ar.edu.utn.frba.dds.dominio.solicitudes.exceptions.modificaciones.NoPuedeModificarHechoUsuarioNoCreadorException;
import ar.edu.utn.frba.dds.dominio.solicitudes.exceptions.modificaciones.NoPuedeModificarHechoUsuarioNoRegistradoException;
import ar.edu.utn.frba.dds.dominio.solicitudes.modificacion.ModificacionHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.modificacion.SolicitudModificacionHecho;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioHechoDinamico;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitud;
import java.time.LocalDateTime;
import java.util.Objects;

public class ProcesadorDeSolicitudesDeModificacion
    extends ProcesadorDeSolicitudes<SolicitudModificacionHecho> {

  private final RepositorioHechoDinamico repoHechos;

  public ProcesadorDeSolicitudesDeModificacion(
      RepositorioSolicitud repoSolicitudes, RepositorioHechoDinamico repoHechos
  ) {
    super(repoSolicitudes);
    this.repoHechos = repoHechos;
  }

  public SolicitudModificacionHecho crearSolicitudDeModificacion(
      Long hechoId,
      String justificacion,
      Usuario solicitante,
      ModificacionHecho modificaciones) {

    // 1. Validar permisos antes de crear la solicitud
    if (!solicitante.estaRegistrado()) {
      throw new NoPuedeModificarHechoUsuarioNoRegistradoException();
    }

    var hechoModificable = repoHechos.consultarPorId(hechoId);

    if (!Objects.equals(hechoModificable.getUsuarioId(), solicitante.getId())) {
      throw new NoPuedeModificarHechoUsuarioNoCreadorException(hechoModificable);
    }

    if (LocalDateTime.now().isAfter(hechoModificable.getFechaCarga().plusWeeks(1))) {
      throw new NoPuedeModificarHechoFueraDePlazoModificacionException();
    }

    // 2. Si las validaciones pasan, se crea la solicitud
    SolicitudModificacionHecho solicitud = new SolicitudModificacionHecho(
        hechoId, justificacion, solicitante, modificaciones);

    // 3. Se persiste la nueva solicitud
    return repoSolicitudes.crear(solicitud);
  }

  @Override
  public void aceptar(SolicitudModificacionHecho solicitud) {
    var modificacion = solicitud.getModificacionHecho();
    var hechoParaModificar = repoHechos.consultarPorId(solicitud.getHechoId());
    modificacion.aplicarA(hechoParaModificar);
    solicitud.aceptar();

    withTransaction(() -> {
      repoHechos.actualizar(hechoParaModificar);
      repoSolicitudes.actualizar(solicitud);
    });
  }
}