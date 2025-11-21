package ar.edu.utn.frba.dds.persistencia;

import ar.edu.utn.frba.dds.dominio.colecciones.Coleccion;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;

public class RepositorioDeColecciones implements WithSimplePersistenceUnit {

  public void crear(Coleccion coleccion) {
    entityManager().persist(coleccion);
  }

  public Coleccion consultarPorId(Long id) {
    return entityManager().find(Coleccion.class, id);
  }

  public List<Coleccion> consultarTodas() {
    return entityManager()
        .createQuery("from Coleccion", Coleccion.class)
        .getResultList();
  }
}
