package ar.edu.utn.frba.dds.dominio.fuentes;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.util.List;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_fuente", discriminatorType = DiscriminatorType.STRING)
public abstract class Fuente {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public abstract List<Hecho> getHechos();

  public List<Hecho> getHechosPaginado(int pagina, int cantidad) {
    return getHechos(); // TODO: quitar una vez hecho el POC.
  }

  // TODO: Ver de cambiarle el nombre para no confundir con getHechos
  public List<Hecho> obtenerHechosPorCriterio(CriterioDePertenencia criterio) {
    return getHechos().stream().filter(criterio::cumple).toList();
  }

  public List<Hecho> obtenerHechosPorCriterioPaginado(
      CriterioDePertenencia criterio,
      int pagina,
      int cantidad
  ) {
    // TODO: quitar una vez hecho el POC.
    return getHechosPaginado(pagina, cantidad).stream().filter(criterio::cumple).toList();
  }

  public Long getId() {
    return id;
  }

  public int cantidadTotalDeHechos() {
    return getHechos().size();
  }
}