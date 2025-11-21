package ar.edu.utn.frba.dds.dominio.colecciones.filtros;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.util.Objects;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Filtro por descripción del hecho.
 */
@Entity
@DiscriminatorValue("DESCRIPCION")
public class FiltroDescripcion extends FiltroHecho {

  private String descripcion;

  public FiltroDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  protected FiltroDescripcion() {

  }

  @Override
  public boolean cumple(Hecho hecho) {
    return hecho.getDescripcion().contains(descripcion);
  }

  @Override
  public Predicate predicado(CriteriaBuilder cb, Root<Hecho> root) {
    return cb.like(root.get("descripcion"), "%" + descripcion + "%");
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FiltroDescripcion that = (FiltroDescripcion) o;
    return Objects.equals(descripcion, that.descripcion);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(descripcion);
  }
}
