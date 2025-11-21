package ar.edu.utn.frba.dds.persistencia;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroHecho;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;

public class RepositorioDeFiltrosHecho implements WithSimplePersistenceUnit {

  public void crear(FiltroHecho filtro) {
    entityManager().persist(filtro);
  }

  public FiltroHecho consultarPorId(Long id) {
    return entityManager().find(FiltroHecho.class, id);
  }

  public List<FiltroHecho> consultarTodos() {
    return entityManager()
        .createQuery("from FiltroHecho", FiltroHecho.class)
        .getResultList();
  }
}
