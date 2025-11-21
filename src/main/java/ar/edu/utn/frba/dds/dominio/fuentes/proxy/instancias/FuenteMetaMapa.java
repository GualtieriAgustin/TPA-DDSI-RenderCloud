package ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.fuentes.cache.FuenteCacheable;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
@DiscriminatorValue("METAMAPA")
public class FuenteMetaMapa extends FuenteCacheable {

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "agregador_subfuentes",
      joinColumns = @JoinColumn(name = "agregador_id"),
      inverseJoinColumns = @JoinColumn(name = "subfuente_id")
  )
  private final List<ServicioMetamapa> servicioMetamapas;

  public FuenteMetaMapa(List<ServicioMetamapa> servicioMetamapas) {
    super();
    this.servicioMetamapas = new ArrayList<>(servicioMetamapas);
  }

  public FuenteMetaMapa() {
    //JPA requiere un constructor vacío
    super();
    this.servicioMetamapas = new ArrayList<>();
  }

  @Override
  public List<Hecho> getHechos() {
    return servicioMetamapas.stream()
        .map(s -> s.getHechos(new CriterioDePertenencia()))
        .flatMap(List::stream)
        .toList();
  }

  // Gran ventaja: se vuelve statelss. No necesita el filtro como atributo.
  @Override
  public List<Hecho> obtenerHechosPorCriterio(CriterioDePertenencia criterio) {
    return servicioMetamapas
        .stream()
        .map(s -> s.getHechos(criterio))
        .flatMap(List::stream)
        .toList();
  }
}
