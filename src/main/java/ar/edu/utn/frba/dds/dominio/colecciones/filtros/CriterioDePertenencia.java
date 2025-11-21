package ar.edu.utn.frba.dds.dominio.colecciones.filtros;

import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos.FiltroHechoRequest;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * CriterioDePertenencia.
 * Tiene una lista de filtros a aplicar para determinar si un hecho pertenece o no.
 */
@Entity
@Table(name = "criterio_de_pertenencia")
public class CriterioDePertenencia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "criterio_id")
  private final List<FiltroHecho> filtros = new ArrayList<>();

  public CriterioDePertenencia() {}

  public CriterioDePertenencia(List<FiltroHecho> filtros) {
    this.filtros.addAll(filtros);
  }

  public CriterioDePertenencia(FiltroHecho filtro) {
    this.filtros.add(filtro);
  }

  public boolean cumple(Hecho hecho) {
    return filtros.stream().allMatch(filtro -> filtro.cumple(hecho));
  }

  public void aplicarA(FiltroHechoRequest filtroHechoRequest) {
    this.filtros.forEach(filtro -> filtro.aplicarA(filtroHechoRequest));
  }

  public List<Predicate> predicados(CriteriaBuilder cb, Root<Hecho> root) {
    return this.filtros.stream()
        .map(filtro -> filtro.predicado(cb, root))
        .toList();
  }

  public Long getId() {
    return id;
  }

  public List<FiltroHecho> getFiltros() {
    return Collections.unmodifiableList(filtros);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CriterioDePertenencia that = (CriterioDePertenencia) o;
    return Objects.equals(filtros, that.filtros);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(filtros);
  }
}
