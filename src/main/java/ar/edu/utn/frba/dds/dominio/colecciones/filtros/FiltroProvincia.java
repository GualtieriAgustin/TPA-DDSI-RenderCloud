package ar.edu.utn.frba.dds.dominio.colecciones.filtros;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Entity
@DiscriminatorValue("PROVINCIA")
public class FiltroProvincia extends FiltroHecho {

  @Enumerated(EnumType.STRING)
  private Provincia provincia;

  public FiltroProvincia(Provincia provincia) {
    this.provincia = provincia;
  }

  protected FiltroProvincia() {
  }

  @Override
  public boolean cumple(Hecho hecho) {
    return hecho.getProvincia() == this.provincia;
  }

  @Override
  public Predicate predicado(CriteriaBuilder cb, Root<Hecho> root) {
    return cb.equal(root.get("provincia"), this.provincia);
  }
}
