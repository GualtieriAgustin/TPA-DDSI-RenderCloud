package ar.edu.utn.frba.dds.persistencia.solicitud;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.EstadoSolicitud;
import ar.edu.utn.frba.dds.dominio.solicitudes.SolicitudHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.baja.SolicitudBajaHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.modificacion.SolicitudModificacionHecho;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class RepositorioSolicitudesEnMemoria implements RepositorioSolicitud {
  private final List<SolicitudHecho> solicitudes = new ArrayList<>();
  private final AtomicLong nextId = new AtomicLong(1);

  @Override
  public <T extends SolicitudHecho> T crear(T solicitud) {
    if (solicitud.getId() == null) {
      try {
        var idField = SolicitudHecho.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(solicitud, nextId.getAndIncrement());
      } catch (NoSuchFieldException | IllegalAccessException e) {
        throw new RuntimeException("No se pudo setear el ID en la solicitud en memoria", e);
      }
    }
    solicitudes.add(solicitud);
    return solicitud;
  }

  @Override
  public <T extends SolicitudHecho> void actualizar(T solicitud) {
    var existingSolicitud = consultarPorId(solicitud.getId());

    if (existingSolicitud != null) {
      solicitudes.remove(existingSolicitud);
      solicitudes.add(solicitud);
    }
  }

  @Override
  public SolicitudHecho consultarPorId(Long id) {
    return solicitudes.stream()
        .filter(s -> s.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<SolicitudHecho> consultarTodas() {
    return Collections.unmodifiableList(solicitudes);
  }

  @Override
  public List<SolicitudHecho> consultarPendientes() {
    return solicitudes.stream()
        .filter(solicitud -> solicitud.getEstado() == EstadoSolicitud.PENDIENTE)
        .collect(Collectors.toList());
  }

  @Override
  public boolean estaActivo(Hecho hecho) {
    boolean coincideConAlgunaBaja = solicitudes.stream()
        .filter(s -> s instanceof SolicitudBajaHecho)
        .map(s -> (SolicitudBajaHecho) s)
        .filter(s -> s.getEstado() == EstadoSolicitud.ACEPTADA)
        .map(SolicitudBajaHecho::getCriterioDeBaja)
        .anyMatch(criterio -> criterio.cumple(hecho));

    return !coincideConAlgunaBaja;
  }

  @Override
  public List<SolicitudModificacionHecho> consultarPorUsuario(Long idUsuario) {
    return solicitudes.stream()
        .filter(s -> s instanceof SolicitudModificacionHecho)
        .map(s -> (SolicitudModificacionHecho) s)
        .filter(s -> s.getUserId().equals(idUsuario))
        .collect(Collectors.toList());
  }

  @Override
  public SolicitudBajaHecho consultarBajaPorId(Long id) {
    SolicitudHecho solicitudGenerica = consultarPorId(id);

    if (solicitudGenerica instanceof SolicitudBajaHecho) {
      return (SolicitudBajaHecho) solicitudGenerica;
    }
    return null;
  }

  @Override
  public SolicitudModificacionHecho consultarModificacionPorId(Long id) {
    SolicitudHecho solicitudGenerica = consultarPorId(id);

    if (solicitudGenerica instanceof SolicitudModificacionHecho) {
      return (SolicitudModificacionHecho) solicitudGenerica;
    }
    return null;
  }
}