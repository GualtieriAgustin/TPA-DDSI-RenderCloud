package ar.edu.utn.frba.dds.dominio.colecciones.filtros;

import ar.edu.utn.frba.dds.dominio.fuentes.Origen;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.util.Objects;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Filtro por origen del hecho.
 */
@Entity
@DiscriminatorValue("ORIGEN")
public class FiltroOrigen extends FiltroHecho {
  private final Origen origen;

  public FiltroOrigen(Origen origen) {
    this.origen = origen;
  }

  @Override
  public boolean cumple(Hecho hecho) {
    return origen.equals(hecho.getOrigen());
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FiltroOrigen that = (FiltroOrigen) o;
    return origen == that.origen;
  }

  @Override
  public Predicate predicado(CriteriaBuilder cb, Root<Hecho> root) {
    return cb.equal(root.get("origen"), this.origen);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(origen);
  }
}
