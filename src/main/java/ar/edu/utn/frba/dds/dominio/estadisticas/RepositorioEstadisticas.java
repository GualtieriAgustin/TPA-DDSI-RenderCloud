package ar.edu.utn.frba.dds.dominio.estadisticas;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;

public class RepositorioEstadisticas implements WithSimplePersistenceUnit {

  public EstadisticaResultado registrar(EstadisticaResultado estadisticaResultado) {
    entityManager().persist(estadisticaResultado);
    return estadisticaResultado;
  }

  public EstadisticaResultado obtener(Long id) {
    return entityManager().find(EstadisticaResultado.class, id);
  }

  public List<EstadisticaResultado> obtenerTodasLasEstadisticas() {
    return entityManager()
        .createQuery("from EstadisticaResultado", EstadisticaResultado.class)
        .getResultList();
  }

  public EstadisticaResultado obtenerUltimaEstadistica() {
    return entityManager()
        .createQuery("from EstadisticaResultado order by id desc", EstadisticaResultado.class)
        .setMaxResults(1)
        .getSingleResult();
  }

}
