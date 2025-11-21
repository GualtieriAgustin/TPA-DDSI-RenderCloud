package ar.edu.utn.frba.dds.dominio.colecciones.filtros;

import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos.FiltroHechoRequest;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.util.Objects;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Filtro por categoría de hecho.
 */
@Entity
@DiscriminatorValue("CATEGORIA")
public class FiltroCategoria extends FiltroHecho {

  private String categoria;

  public FiltroCategoria(String categoria) {
    this.categoria = categoria;
  }

  protected FiltroCategoria() {

  }

  @Override
  public boolean cumple(Hecho hecho) {
    return categoria.equals(hecho.getCategoria());
  }

  @Override
  public void aplicarA(FiltroHechoRequest filtroHechoRequest) {
    filtroHechoRequest.conCategoria(categoria);
  }

  @Override
  public Predicate predicado(CriteriaBuilder cb, Root<Hecho> root) {
    return cb.equal(root.get("categoria"), this.categoria);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FiltroCategoria that = (FiltroCategoria) o;
    return Objects.equals(categoria, that.categoria);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(categoria);
  }
}
