package ar.edu.utn.frba.dds.dominio.colecciones.filtros.fecha.rango;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroHecho;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class FiltroFechaRango extends FiltroHecho {

  protected LocalDateTime fechaDesde;
  protected LocalDateTime fechaHasta;
  private String campoFecha;

  protected FiltroFechaRango() {
    super();
  }

  FiltroFechaRango(LocalDateTime fechaDesde, LocalDateTime fechaHasta, String campoFecha) {
    this.fechaDesde = fechaDesde;
    this.fechaHasta = fechaHasta;
    this.campoFecha = campoFecha;
  }

  abstract LocalDateTime fechaComparacion(Hecho hecho);

  @Override
  public boolean cumple(Hecho hecho) {
    return fechaComparacion(hecho).isAfter(fechaDesde)
        && fechaComparacion(hecho).isBefore(fechaHasta);
  }

  @Override
  public Predicate predicado(CriteriaBuilder cb, Root<Hecho> root) {
    return cb.between(root.get(campoFecha), fechaDesde, fechaHasta);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FiltroFechaRango that = (FiltroFechaRango) o;
    return Objects.equals(fechaDesde, that.fechaDesde)
        && Objects.equals(fechaHasta, that.fechaHasta);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fechaDesde, fechaHasta);
  }
}
