package ar.edu.utn.frba.dds.persistencia.solicitud;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.EstadoSolicitud;
import ar.edu.utn.frba.dds.dominio.solicitudes.SolicitudHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.baja.SolicitudBajaHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.modificacion.SolicitudModificacionHecho;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;

public class RepositorioSolicitudes implements RepositorioSolicitud, WithSimplePersistenceUnit {

  @Override
  public <T extends SolicitudHecho> T crear(T solicitud) {
    entityManager().persist(solicitud);
    return solicitud;
  }

  @Override
  public <T extends SolicitudHecho> void actualizar(T solicitud) {
    entityManager().merge(solicitud);
  }

  @Override
  public SolicitudHecho consultarPorId(Long id) {
    return entityManager().find(SolicitudHecho.class, id);
  }

  @Override
  public List<SolicitudHecho> consultarTodas() {
    return entityManager().createQuery("from SolicitudHecho", SolicitudHecho.class).getResultList();
  }

  @Override
  public List<SolicitudHecho> consultarPendientes() {
    return entityManager()
        .createQuery("from SolicitudHecho where estado = :estado", SolicitudHecho.class)
        .setParameter("estado", EstadoSolicitud.PENDIENTE)
        .getResultList();
  }

  @Override
  public boolean estaActivo(Hecho hecho) {
    List<CriterioDePertenencia> criteriosDeBaja = entityManager()
        .createQuery(
            "select s.criterioDeBaja from SolicitudBajaHecho s where s.estado = :estado",
            CriterioDePertenencia.class)
        .setParameter("estado", EstadoSolicitud.ACEPTADA)
        .getResultList();

    boolean coincideConAlgunaBaja = criteriosDeBaja.stream()
        .anyMatch(criterio -> criterio.cumple(hecho));

    return !coincideConAlgunaBaja;
  }

  @Override
  public List<SolicitudModificacionHecho> consultarPorUsuario(Long idUsuario) {
    return entityManager()
        .createQuery(
            "from SolicitudModificacionHecho s where usuario = :idUsuario",
            SolicitudModificacionHecho.class)
        .setParameter("idUsuario", idUsuario)
        .getResultList();
  }

  @Override
  public SolicitudBajaHecho consultarBajaPorId(Long id) {
    return entityManager().find(SolicitudBajaHecho.class, id);
  }

  @Override
  public SolicitudModificacionHecho consultarModificacionPorId(Long id) {
    return entityManager().find(SolicitudModificacionHecho.class, id);
  }
}