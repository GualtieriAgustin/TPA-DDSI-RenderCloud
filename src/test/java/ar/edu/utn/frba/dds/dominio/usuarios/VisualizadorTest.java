package ar.edu.utn.frba.dds.dominio.usuarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ar.edu.utn.frba.dds.dominio.colecciones.Coleccion;
import ar.edu.utn.frba.dds.dominio.colecciones.ModoDeNavegacion;
import ar.edu.utn.frba.dds.dominio.colecciones.NavegadorDeColecciones;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.fuentes.Fuente;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitud;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitudesEnMemoria;
import ar.edu.utn.frba.dds.utils.archivos.ImportarHechos;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VisualizadorTest {

  RepositorioSolicitud repoSolicitudes;
  Fuente fuente;

  @BeforeEach
  void setUp() {
    repoSolicitudes = new RepositorioSolicitudesEnMemoria();
    fuente = ImportarHechos.deArchivo("accidentes_transito_fatales_min.csv");
    Coleccion coleccion = new Coleccion(
        "accidentes transito",
        "accidentes de transito fatales",
        fuente,
        new CriterioDePertenencia(),
        ModoDeNavegacion.IRRESTRICTO
    );
  }

  @Test
  public void verHechosDeColeccion_CuandoSeNaveganTodosLosHechos_DeberiaRetornarTodosLosHechos() {
    // Arrange
    NavegadorDeColecciones navegador = new NavegadorDeColecciones(repoSolicitudes);

    // Act
    List<Hecho> hechos = fuente.getHechos();

    // Assert
    assertEquals(8, hechos.size(), "Se esperaban todos los hechos");
    hechos.forEach(hecho -> {
      assertNotNull(hecho.getTitulo());
      assertNotNull(hecho.getDescripcion());
      assertNotNull(hecho.getCategoria());
      assertNotNull(hecho.getUbicacion());
      assertNotNull(hecho.getOrigen());
      assertNotNull(hecho.getFechaCarga());
      assertNotNull(hecho.getFechaSuceso());
    });
  }

  @Test
  public void verHechosDeColeccionAplicandoFiltros_CuandoSeAplicaFiltroCategoria_DeberiaRetornarHechosFiltrados() {

  }

  @Test
  public void verHechosDeColeccionAplicandoFiltros_CuandoSeAplicaFiltroCategoriaYUbicacion_DeberiaRetornarHechosFiltrados() {

  }

  @Test
  public void verHechosDeColeccionAplicandoFiltros_CuandoSeAplicaFiltroCategoriaDescripcionYUbicacion_DeberiaRetornarHechosFiltrados() {

  }

  // Entrega 3 - Requerimiento 1
  @Test
  public void comoPersonaVisualizadora_puedoSeleccionarElModoDeNavegacionDeLosHechos_curado(){

  }

  // Entrega 3 - Requerimiento 1
  @Test
  public void comoPersonaVisualizadora_puedoSeleccionarElModoDeNavegacionDeLosHechos_irrestricto(){

  }
}