package ar.edu.utn.frba.dds.dominio.colecciones.filtros;

import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos.FiltroHechoRequest;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import java.util.Objects;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Filtro por ubicación.
 */
@Entity
@DiscriminatorValue("UBICACION")
public class FiltroUbicacion extends FiltroHecho {

  @Embedded
  private Ubicacion ubicacion;

  public FiltroUbicacion(Ubicacion ubicacion) {
    this.ubicacion = ubicacion;
  }

  protected FiltroUbicacion() {

  }

  @Override
  public boolean cumple(Hecho hecho) {
    return ubicacion.equals(hecho.getUbicacion());
  }

  @Override
  public void aplicarA(FiltroHechoRequest filtroHechoRequest) {
    filtroHechoRequest.conUbicacion(ubicacion);
  }

  @Override
  public Predicate predicado(CriteriaBuilder cb, Root<Hecho> root) {
    return cb.equal(root.get("ubicacion"), this.ubicacion);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FiltroUbicacion that = (FiltroUbicacion) o;
    return Objects.equals(ubicacion, that.ubicacion);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(ubicacion);
  }
}
