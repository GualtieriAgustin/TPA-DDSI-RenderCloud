package ar.edu.utn.frba.dds.persistencia.repositorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeUsuarios;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RepositorioDeUsuariosTest implements SimplePersistenceTest {

  private RepositorioDeUsuarios repositorioDeUsuarios;

  @BeforeEach
  void setUp() {
    repositorioDeUsuarios = new RepositorioDeUsuarios();
  }

  @Test
  void sePuedeRegistrarYRecuperarUnUsuario() {
    Usuario usuario = new Usuario("pepito");

    repositorioDeUsuarios.registrar(usuario);

    Usuario usuarioRecuperado = entityManager().find(Usuario.class, usuario.getId());

    assertNotNull(usuarioRecuperado);
    assertEquals("pepito", usuarioRecuperado.getNombre());
  }

  @Test
  void sePuedenConsultarTodosLosUsuarios() {
    repositorioDeUsuarios.registrar(new Usuario("pepito"));
    repositorioDeUsuarios.registrar(new Usuario("juanita"));

    List<Usuario> usuarios = repositorioDeUsuarios.todos();
    assertEquals(2, usuarios.size());
  }

  @Test
  void sePuedeFiltrarUsuariosPorNombre() {
    repositorioDeUsuarios.registrar(new Usuario("pepito"));
    repositorioDeUsuarios.registrar(new Usuario("juanita"));

    List<Usuario> usuariosFiltrados = repositorioDeUsuarios.filtrarPorNombre("pepito");
    assertEquals(1, usuariosFiltrados.size());
    assertEquals("pepito", usuariosFiltrados.get(0).getNombre());
  }
}