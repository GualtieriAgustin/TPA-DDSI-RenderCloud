package ar.edu.utn.frba.dds.persistencia;

import ar.edu.utn.frba.dds.dominio.fuentes.Fuente;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;
import java.util.Optional;

public class RepositorioDeFuentes implements WithSimplePersistenceUnit {

  public List<Fuente> buscarTodas() {
    return entityManager()
        .createQuery("from Fuente", Fuente.class)
        .getResultList();
  }

  public Optional<Fuente> buscarPorId(Long id) {
    return Optional.ofNullable(entityManager().find(Fuente.class, id));
  }

  public <T extends Fuente> List<T> buscarPorTipo(Class<T> tipo) {
    // El nombre de la entidad es el nombre de la clase por defecto.
    String nombreEntidad = tipo.getSimpleName();
    return entityManager()
        .createQuery("from " + nombreEntidad, tipo)
        .getResultList();
  }

  public void guardar(Fuente fuente) {
    withTransaction(() -> entityManager().persist(fuente));
  }

  public void eliminar(Fuente fuente) {
    withTransaction(() -> entityManager().remove(fuente));
  }
}
