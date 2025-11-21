package ar.edu.utn.frba.dds.persistencia;

import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;

public class RepositorioDeUsuarios implements WithSimplePersistenceUnit {
  public void registrar(Usuario usuario) {
    entityManager().persist(usuario);
  }

  @SuppressWarnings("unchecked")
  public List<Usuario> todos() {
    return entityManager()
        .createQuery("from Usuario")
        .getResultList();
  }

  @SuppressWarnings("unchecked")
  public List<Usuario> filtrarPorNombre(String nombre) {
    return entityManager()
        .createQuery("from Usuario where nombre = :nombre")
        .setParameter("nombre", nombre)
        .getResultList();
  }

  public Usuario buscarUsuario(String username) {
    return entityManager()
            .createQuery("from Usuario where username = :username", Usuario.class)
            .setParameter("username", username)
            .getSingleResult();
  }

}
