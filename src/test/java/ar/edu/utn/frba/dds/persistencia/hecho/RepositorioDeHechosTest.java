package ar.edu.utn.frba.dds.persistencia.hecho;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroCategoria;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RepositorioDeHechosTest implements SimplePersistenceTest {

  private RepositorioDeHechos repositorioDeHechos;

  @BeforeEach
  void setUp() {
    repositorioDeHechos = new RepositorioDeHechos();
  }

  @Test
  void sePuedePersistirYRecuperarUnHechoPorId() {
    Hecho hechoAPersistir = new HechoTestBuilder().conTitulo("Incendio en el Obelisco").build();

    repositorioDeHechos.crear(hechoAPersistir);
    
    Hecho hechoRecuperado = repositorioDeHechos.consultarPorId(hechoAPersistir.getId());
    assertNotNull(hechoRecuperado);
    assertEquals("Incendio en el Obelisco", hechoRecuperado.getTitulo());
  }

  @Test
  void consultarTodosDevuelveTodosLosHechosPersistidos() {
    Hecho hecho1 = new HechoTestBuilder().conTitulo("Hecho 1").build();
    Hecho hecho2 = new HechoTestBuilder().conTitulo("Hecho 2").build();

    repositorioDeHechos.crear(hecho1);
    repositorioDeHechos.crear(hecho2);

    List<Hecho> hechos = repositorioDeHechos.consultarTodos();
    assertEquals(2, hechos.size());
  }

  @Test
  void consultarPorCriterioFiltraCorrectamentePorCategoria() {
    Hecho hechoDeAccidente = new HechoTestBuilder().conCategoria("accidente").conTitulo("Choque en la 9 de Julio").build();
    Hecho hechoDeIncendio = new HechoTestBuilder().conCategoria("incendio").conTitulo("Fuego en Palermo").build();
    CriterioDePertenencia criterio = new CriterioDePertenencia(new FiltroCategoria("accidente"));

    repositorioDeHechos.crear(hechoDeAccidente);
    repositorioDeHechos.crear(hechoDeIncendio);

    List<Hecho> hechosFiltrados = repositorioDeHechos.consultarPorCriterio(criterio);

    assertEquals(1, hechosFiltrados.size());
    assertEquals("Choque en la 9 de Julio", hechosFiltrados.get(0).getTitulo());
  }

  @Test
  void consultarPorUsuarioDevuelveSoloLosHechosDeEseUsuario() {
    Usuario usuario1 = new Usuario("pepito");
    Usuario usuario2 = new Usuario("juanita");
    Hecho hechoUsuario1 = new HechoTestBuilder().conUsuario(usuario1).conTitulo("Hecho de Pepito").build();
    Hecho hechoUsuario2 = new HechoTestBuilder().conUsuario(usuario2).conTitulo("Hecho de Juanita").build();

    entityManager().persist(usuario1);
    entityManager().persist(usuario2);
    repositorioDeHechos.crear(hechoUsuario1);
    repositorioDeHechos.crear(hechoUsuario2);

    List<Hecho> hechosDeUsuario1 = repositorioDeHechos.consultarPorUsuario(usuario1.getId());

    assertEquals(1, hechosDeUsuario1.size());
    assertEquals("Hecho de Pepito", hechosDeUsuario1.get(0).getTitulo());
  }
}