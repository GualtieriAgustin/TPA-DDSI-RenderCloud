package ar.edu.utn.frba.dds.dominio.colecciones.filtros;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.util.Objects;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Filtro por título.
 * Matchea si contiene al título dado, es el mismo título, o bien el hecho no tiene título.
 */
@Entity
@DiscriminatorValue("TITULO")
public class FiltroTitulo extends FiltroHecho {

  private String titulo;

  public FiltroTitulo(String titulo) {
    this.titulo = titulo;
  }

  protected FiltroTitulo() {

  }

  @Override
  public boolean cumple(Hecho hecho) {
    return hecho.getTitulo().contains(titulo);
  }

  @Override
  public Predicate predicado(CriteriaBuilder cb, Root<Hecho> root) {
    return cb.like(root.get("titulo"), "%" + titulo + "%");
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FiltroTitulo that = (FiltroTitulo) o;
    return Objects.equals(titulo, that.titulo);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(titulo);
  }
}
