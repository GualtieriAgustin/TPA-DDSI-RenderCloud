package ar.edu.utn.frba.dds.dominio.fuentes;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.fuentes.cache.FuenteCacheable;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
@DiscriminatorValue("AGREGADOR")
public class Agregador extends FuenteCacheable {

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "agregador_subfuentes",
      joinColumns = @JoinColumn(name = "agregador_id"),
      inverseJoinColumns = @JoinColumn(name = "subfuente_id")
  )
  private final List<Fuente> fuentes = new ArrayList<>();

  public Agregador() {
    super();
  }

  public Agregador(List<Fuente> fuentes) {
    this.fuentes.addAll(fuentes);
  }

  public void agregarFuente(Fuente fuente) {
    fuentes.add(fuente);
  }

  @Override
  public List<Hecho> getHechos() {
    return fuentes.stream()
        .flatMap(this::safeGetHechos)
        .toList();
  }

  @Override
  public List<Hecho> obtenerHechosPorCriterio(CriterioDePertenencia criterio) {
    return fuentes.stream()
        .flatMap(fuente -> fuente.obtenerHechosPorCriterio(criterio).stream())
        .toList();
  }

  private Stream<Hecho> safeGetHechos(Fuente fuente) {
    try {
      return fuente.getHechos().stream();
    } catch (Exception e) {
      System.err.println("Error al obtener hechos de la fuente: " + e.getMessage());
      return Stream.empty();
    }
  }
}
