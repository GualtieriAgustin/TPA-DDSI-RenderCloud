package ar.edu.utn.frba.dds.persistencia.solicitud;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.SolicitudHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.baja.SolicitudBajaHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.modificacion.SolicitudModificacionHecho;
import java.util.List;

public interface RepositorioSolicitud {

  <T extends SolicitudHecho> T crear(T solicitud);

  <T extends SolicitudHecho> void actualizar(T solicitud);

  SolicitudHecho consultarPorId(Long id);

  List<SolicitudHecho> consultarTodas();

  List<SolicitudHecho> consultarPendientes();

  boolean estaActivo(Hecho hecho);

  List<SolicitudModificacionHecho> consultarPorUsuario(Long idUsuario);

  SolicitudBajaHecho consultarBajaPorId(Long id);

  SolicitudModificacionHecho consultarModificacionPorId(Long id);
}