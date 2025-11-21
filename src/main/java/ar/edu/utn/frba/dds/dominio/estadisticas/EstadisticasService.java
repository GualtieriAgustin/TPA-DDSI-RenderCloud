package ar.edu.utn.frba.dds.dominio.estadisticas;

import ar.edu.utn.frba.dds.dominio.estadisticas.querys.EstadisticaQuery;
import java.time.LocalDateTime;
import java.util.List;

public interface EstadisticasService {
  void generarEstadisticas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

  List<EstadisticaResultado> obtenerUltimaEstadistica();

  void addQuery(EstadisticaQuery query);
}
