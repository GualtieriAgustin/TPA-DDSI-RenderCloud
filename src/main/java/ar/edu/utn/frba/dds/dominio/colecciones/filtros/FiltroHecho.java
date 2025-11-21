package ar.edu.utn.frba.dds.dominio.colecciones.filtros;

import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos.FiltroHechoRequest;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Entity
@Table(name = "filtro_hecho")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_filtro", discriminatorType = DiscriminatorType.STRING)
public abstract class FiltroHecho {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public abstract boolean cumple(Hecho hecho);

  public void aplicarA(FiltroHechoRequest filtroHechoRequest) {

  }

  /**
   * Traduce el filtro de dominio a un predicado de JPA Criteria API.
   * Por defecto, lanza una excepción para forzar la implementación en los filtros
   * que deban ser usados en consultas a la base de datos.
   *
   * @param cb El CriteriaBuilder para construir el predicado.
   * @param root La raíz de la consulta, representa la tabla Hecho.
   * @return Un predicado que puede ser usado en una cláusula WHERE.
   */
  public Predicate predicado(CriteriaBuilder cb, Root<Hecho> root) {
    throw new UnsupportedOperationException("Este filtro no soporta consulta de base de datos.");
  }

  public Long getId() {
    return id;
  }
}
