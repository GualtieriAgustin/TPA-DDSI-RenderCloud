package ar.edu.utn.frba.dds.persistencia;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;
import java.util.Optional;

public class RepositorioDeCriteriosDePertenencia implements WithSimplePersistenceUnit {

  public void crear(CriterioDePertenencia criterio) {
    entityManager().persist(criterio);
  }

  public CriterioDePertenencia consultarPorId(Long id) {
    return entityManager().find(CriterioDePertenencia.class, id);
  }

  public List<CriterioDePertenencia> consultarTodos() {
    return entityManager()
        .createQuery("from CriterioDePertenencia", CriterioDePertenencia.class)
        .getResultList();
  }

  public Optional<CriterioDePertenencia> buscarPorId(Long id) {
    return Optional.ofNullable(entityManager().find(CriterioDePertenencia.class, id));
  }
}
