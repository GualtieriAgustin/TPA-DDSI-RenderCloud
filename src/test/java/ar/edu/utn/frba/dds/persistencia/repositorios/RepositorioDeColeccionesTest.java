package ar.edu.utn.frba.dds.persistencia.repositorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ar.edu.utn.frba.dds.dominio.colecciones.Coleccion;
import ar.edu.utn.frba.dds.dominio.colecciones.ModoDeNavegacion;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.fuentes.FuenteDinamica;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeColecciones;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioDeHechos;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RepositorioDeColeccionesTest implements SimplePersistenceTest {

  private RepositorioDeColecciones repositorioDeColecciones;

  @BeforeEach
  void setUp() {
    repositorioDeColecciones = new RepositorioDeColecciones();
    withTransaction(() -> {
      entityManager().createQuery("DELETE FROM Coleccion").executeUpdate();
    });
  }

  @Test
  void sePuedeCrearYRecuperarUnaColeccionPorId() {
    Coleccion coleccion = new Coleccion(
        "Incendios 2025",
        "Colección de incendios",
        new FuenteDinamica(new RepositorioDeHechos()),
        new CriterioDePertenencia(),
        ModoDeNavegacion.CURADO
    );

    withTransaction(() -> repositorioDeColecciones.crear(coleccion));

    Coleccion coleccionRecuperada = repositorioDeColecciones.consultarPorId(coleccion.getId());

    assertNotNull(coleccionRecuperada);
    assertEquals("Incendios 2025", coleccionRecuperada.getTitulo());
    assertEquals(ModoDeNavegacion.CURADO, coleccionRecuperada.getModoDeNavegacion());
  }

  @Test
  void sePuedenConsultarTodasLasColecciones() {
    Coleccion coleccion1 = new Coleccion(
        "Coleccion 1",
        "desc 1",
        new FuenteDinamica(new RepositorioDeHechos()),
        new CriterioDePertenencia(),
        ModoDeNavegacion.IRRESTRICTO
    );
    Coleccion coleccion2 = new Coleccion(
        "Coleccion 2",
        "desc 2",
        new FuenteDinamica(new RepositorioDeHechos()),
        new CriterioDePertenencia(),
        ModoDeNavegacion.CURADO
    );

    withTransaction(() -> {
      repositorioDeColecciones.crear(coleccion1);
      repositorioDeColecciones.crear(coleccion2);
    });

    List<Coleccion> colecciones = repositorioDeColecciones.consultarTodas();
    assertEquals(2, colecciones.size());
  }
}