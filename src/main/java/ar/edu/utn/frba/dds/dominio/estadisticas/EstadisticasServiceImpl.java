package ar.edu.utn.frba.dds.dominio.estadisticas;

import ar.edu.utn.frba.dds.dominio.estadisticas.querys.EstadisticaQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstadisticasServiceImpl implements EstadisticasService {

  private final RepositorioEstadisticas repositorioEstadisticas;

  public EstadisticasServiceImpl(RepositorioEstadisticas repositorioEstadisticas) {
    this.repositorioEstadisticas = repositorioEstadisticas;
  }

  private Map<String, EstadisticaResultado> ultimaEstadistica
      = new HashMap<>();

  private List<EstadisticaQuery> listadoQuerys = new ArrayList<>();

  public List<EstadisticaQuery> getListadoQuerys() {
    return new ArrayList<>(listadoQuerys);
  }

  public void addQuery(EstadisticaQuery query) {
    this.listadoQuerys.add(query);
  }

  //Se cronea esta clase
  public void generarEstadisticas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
    for (EstadisticaQuery q : listadoQuerys) {
      EstadisticaResultado resultado = q.generarEstadisticas(fechaInicio, fechaFin);
      resultado = repositorioEstadisticas.registrar(resultado);
      ultimaEstadistica.put(resultado.getNombre(), resultado);
      EstadisticaResultado finalResultado = resultado;
      repositorioEstadisticas.withTransaction(() ->
              repositorioEstadisticas.registrar(finalResultado));
    }

  }

  public List<EstadisticaResultado> obtenerUltimaEstadistica() {
    return new ArrayList<>(ultimaEstadistica.values());
  }
}