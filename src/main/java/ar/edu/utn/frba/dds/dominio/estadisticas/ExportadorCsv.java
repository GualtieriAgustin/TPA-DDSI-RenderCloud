package ar.edu.utn.frba.dds.dominio.estadisticas;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("EI_EXPOSE_REP2")
public class ExportadorCsv {

  private final EstadisticasService estadisticasService;

  @SuppressWarnings("EI_EXPOSE_REP2")
  public ExportadorCsv(EstadisticasService estadisticasService) {
    this.estadisticasService = estadisticasService;
  }

  public String generarReporteCsv() {
    return formatearCsv(estadisticasService.obtenerUltimaEstadistica());
  }


  private String formatearCsv(List<EstadisticaResultado> estadisticas) {
    String encabezado = "nombre;resultado;fechaCreacion;fechaBusquedaInicio;fechaBusquedaFin";

    String filas = estadisticas.stream()
            .map(e -> String.format("%s;%s;%s;%s;%s",
                    e.getNombre(),
                    e.getResultado(),
                    e.fechaCreacion != null ? e.fechaCreacion.toString() : "",
                    e.getFechaBusquedaInicio(),
                    e.getFechaBusquedaFin()
            ))
            .collect(Collectors.joining("\n"));

    return encabezado + "\n" + filas;
  }

  public String guardarReporteCsv() {
    return generarReporteCsv();
  }
}