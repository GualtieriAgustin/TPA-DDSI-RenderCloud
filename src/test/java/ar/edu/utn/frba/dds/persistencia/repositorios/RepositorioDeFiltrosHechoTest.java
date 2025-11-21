package ar.edu.utn.frba.dds.persistencia.repositorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroCategoria;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroHecho;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroTitulo;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeFiltrosHecho;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RepositorioDeFiltrosHechoTest implements SimplePersistenceTest {

  private RepositorioDeFiltrosHecho repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new RepositorioDeFiltrosHecho();
    withTransaction(() -> {
      entityManager().createQuery("DELETE FROM FiltroHecho").executeUpdate();
    });
  }

  @Test
  void sePuedeCrearYRecuperarUnFiltro() {
    FiltroHecho filtro = new FiltroCategoria("accidente");

    withTransaction(() -> {
      repositorio.crear(filtro);
    });

    FiltroHecho filtroRecuperado = repositorio.consultarPorId(filtro.getId());

    assertNotNull(filtroRecuperado);
    assertInstanceOf(FiltroCategoria.class, filtroRecuperado);
  }

  @Test
  void sePuedenConsultarTodosLosFiltrosDeDistintosTipos() {
    FiltroHecho filtro1 = new FiltroCategoria("incendio");
    FiltroHecho filtro2 = new FiltroTitulo("demora");

    withTransaction(() -> {
      repositorio.crear(filtro1);
      repositorio.crear(filtro2);
    });

    List<FiltroHecho> filtros = repositorio.consultarTodos();
    assertEquals(2, filtros.size());
  }
}