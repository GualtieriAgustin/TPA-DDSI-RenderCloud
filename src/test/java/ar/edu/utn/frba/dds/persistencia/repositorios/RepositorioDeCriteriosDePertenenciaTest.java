package ar.edu.utn.frba.dds.persistencia.repositorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroCategoria;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroHecho;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroTitulo;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeCriteriosDePertenencia;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RepositorioDeCriteriosDePertenenciaTest implements SimplePersistenceTest {

  private RepositorioDeCriteriosDePertenencia repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new RepositorioDeCriteriosDePertenencia();
    withTransaction(() -> {
      entityManager().createQuery("DELETE FROM Coleccion").executeUpdate();
      entityManager().createQuery("DELETE FROM FiltroHecho").executeUpdate();
      entityManager().createQuery("DELETE FROM CriterioDePertenencia").executeUpdate();
    });
  }

  @Test
  void sePuedeCrearYRecuperarUnCriterioConSusFiltros() {
    FiltroHecho filtroCategoria = new FiltroCategoria("accidente");
    FiltroHecho filtroTitulo = new FiltroTitulo("choque");
    CriterioDePertenencia criterio = new CriterioDePertenencia(List.of(filtroCategoria, filtroTitulo));

    withTransaction(() -> {
      repositorio.crear(criterio);
    });
    CriterioDePertenencia criterioRecuperado = repositorio.consultarPorId(criterio.getId());

    assertNotNull(criterioRecuperado);

    assertEquals(2, criterioRecuperado.getFiltros().size());
  }

  @Test
  void sePuedenConsultarTodosLosCriterios() {
    CriterioDePertenencia criterio1 = new CriterioDePertenencia(new FiltroCategoria("incendio"));
    CriterioDePertenencia criterio2 = new CriterioDePertenencia(new FiltroTitulo("demora"));

    withTransaction(() -> {
      repositorio.crear(criterio1);
      repositorio.crear(criterio2);
    });

    List<CriterioDePertenencia> criterios = repositorio.consultarTodos();
    assertEquals(2, criterios.size());
  }
}