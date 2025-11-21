package ar.edu.utn.frba.dds.dominio.colecciones.filtros.fecha.exacta;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroHecho;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Filtra un hecho por fecha entre una fecha inicio y otra final.
 * Puede extenderse en un futuro para comparar contra un intervalo de tiempo.
 */
abstract class FiltroFecha extends FiltroHecho {

  private LocalDateTime fecha;

  protected FiltroFecha() {
    super();
  }

  public FiltroFecha(LocalDateTime fecha) {
    this.fecha = fecha;
  }

  abstract LocalDateTime fechaComparacion(Hecho hecho);

  @Override
  public boolean cumple(Hecho hecho) {
    return fecha.equals(fechaComparacion(hecho));
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FiltroFecha that = (FiltroFecha) o;
    return Objects.equals(fecha, that.fecha);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(fecha);
  }
}
