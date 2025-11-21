package ar.edu.utn.frba.dds.dominio.colecciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ar.edu.utn.frba.dds.dominio.archivos.LectorArchivo;
import ar.edu.utn.frba.dds.dominio.archivos.LectorArchivoCsv;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroCategoria;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroHecho;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroTitulo;
import ar.edu.utn.frba.dds.dominio.consenso.NivelDeConsenso;
import ar.edu.utn.frba.dds.dominio.fuentes.Fuente;
import ar.edu.utn.frba.dds.dominio.fuentes.FuenteEstatica;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitud;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ColeccionTest {

  private Fuente fuenteMock;
  private RepositorioSolicitud mockRepoSolicitud;
  private NavegadorDeColecciones navegador;

  @BeforeEach
  public void setUp() {
    fuenteMock = mock(Fuente.class);
    mockRepoSolicitud = mock(RepositorioSolicitud.class);
    navegador = new NavegadorDeColecciones(mockRepoSolicitud);
  }

  @Test
  public void getHechos_CuandoHechosCumplenFiltros_DebeRetornarListaFiltrada() {
    // Arrange
    CriterioDePertenencia criterio = new CriterioDePertenencia(List.of(new FiltroCategoria("Accidente")));

    Coleccion coleccion = new Coleccion("Colección de Accidentes", "Descripción", fuenteMock, criterio, ModoDeNavegacion.IRRESTRICTO);

    FiltroHecho filtroAdicional = new FiltroTitulo("Incendio");
    Hecho hecho1 = new HechoTestBuilder()
        .conTitulo("Incendio en edificio")
        .conDescripcion("Un incendio ocurrió en un edificio")
        .conCategoria("Accidente")
        .build();
    Hecho hecho2 = new HechoTestBuilder()
        .conTitulo("Choque de autos")
        .conDescripcion("Un choque ocurrió en la autopista")
        .conCategoria("Accidente")
        .build();

    when(mockRepoSolicitud.estaActivo(any())).thenReturn(true);
    when(fuenteMock.getHechos()).thenReturn(List.of(hecho1, hecho2));

    // Act
    List<Hecho> hechosFiltrados = navegador.navegar(coleccion, List.of(filtroAdicional));

    // Assert
    assertEquals(1, hechosFiltrados.size());
    assertEquals("Incendio en edificio", hechosFiltrados.get(0).getTitulo());
  }


  @Test
  public void crearColeccionYRetornarHechosDeUnaFuenteEstatica_CSV() {
    // Arrange
    // Instancio lector y fuente
    var pathCsv = "accidentes_transito_fatales.csv";
    LectorArchivo lector = new LectorArchivoCsv(pathCsv, null);
    FuenteEstatica fuenteReal = new FuenteEstatica(lector);
    var cantidadEsperada = 14788;

    when(mockRepoSolicitud.estaActivo(any())).thenReturn(true);

    // Act
    // Creo coleccion con la fuente
    Coleccion coleccion = new Coleccion(
        "Coleccion test",
        "Coleccion de prueba",
        fuenteReal,
        new CriterioDePertenencia(new FiltroCategoria("Accidente de tránsito")),
        ModoDeNavegacion.IRRESTRICTO
    );

    // El navegador usará el mockRegistroFuentes configurado arriba
    List<Hecho> hechosDeLaColeccion = navegador.navegar(coleccion);


    // Assert
    // Valido que la lista no sea null
    assertNotNull(hechosDeLaColeccion);
    // Valido que la coleccion retorne varios hechos de la fuente
    assertEquals(cantidadEsperada, hechosDeLaColeccion.size());

    hechosDeLaColeccion.forEach(hecho -> {
      assertEquals("Accidente de tránsito", hecho.getCategoria(),
          "Los hechos del archivo csv deberían tener la misma categoria");
    });
  }

  @Test
  public void navegarEnModoCurado_SoloRetornaHechosConsensuados() {
    // Arrange
    // La colección no necesita un criterio de pertenencia para este test.
    Coleccion coleccionCurada = new Coleccion(
        "Colección Curada","Descripción",
        fuenteMock,
        new CriterioDePertenencia(new FiltroCategoria("Curado")),
        ModoDeNavegacion.CURADO);

    Hecho hechoConsensuado = new HechoTestBuilder()
        .conTitulo("Consensuado")
        .conCategoria("Curado")
        .build();

    Hecho hechoNoConsensuado = new HechoTestBuilder()
        .conTitulo("No Consensuado")
        .conCategoria("Cualquiera")
        .build();

    // Simulamos que la fuente provee ambos hechos.
    when(fuenteMock.getHechos()).thenReturn(List.of(hechoConsensuado, hechoNoConsensuado));
    // Simulamos que ambos hechos están activos (no tienen solicitudes de eliminación pendientes).
    when(mockRepoSolicitud.estaActivo(any())).thenReturn(true);

    // Actualizamos el mapa de consenso de la colección.
    Map<Hecho, NivelDeConsenso> resultados = Map.of(
        hechoConsensuado, NivelDeConsenso.CONSENSUADO,
        hechoNoConsensuado, NivelDeConsenso.NO_CONSENSUADO
    );
    coleccionCurada.actualizarConsenso(resultados);

    // Act
    // Navegamos la colección sin filtros adicionales.
    List<Hecho> hechosFiltrados = navegador.navegar(coleccionCurada);

    // Assert
    assertEquals(1, hechosFiltrados.size(), "Solo debería retornar el hecho consensuado.");
    assertEquals("Consensuado", hechosFiltrados.get(0).getTitulo());
  }

  @Test
  public void navegarColeccion_NoDebeRetornarHechosInactivos() {
    // Arrange
    Coleccion coleccion = new Coleccion(
        "Colección con inactivos", "Descripción",
        fuenteMock,
        new CriterioDePertenencia(List.of()), // Sin criterio, todos pertenecen
        ModoDeNavegacion.IRRESTRICTO);

    Hecho hechoActivo = new HechoTestBuilder().conTitulo("Activo").build();
    Hecho hechoInactivo = new HechoTestBuilder().conTitulo("Inactivo").build();

    // Configuramos el mock para que un hecho esté activo y el otro no.
    when(mockRepoSolicitud.estaActivo(hechoActivo)).thenReturn(true);
    when(mockRepoSolicitud.estaActivo(hechoInactivo)).thenReturn(false);

    when(fuenteMock.getHechos()).thenReturn(List.of(hechoActivo, hechoInactivo));

    // Act
    List<Hecho> hechosNavegados = navegador.navegar(coleccion);

    // Assert
    assertEquals(1, hechosNavegados.size(), "Solo el hecho activo debería ser retornado.");
    assertEquals("Activo", hechosNavegados.get(0).getTitulo());
  }

  @Test
  public void obtenerTodosLosHechosActivos_DebeCombinarHechosDeMultiplesFuentes() {
    // Arrange
    Fuente fuenteMock1 = mock(Fuente.class);
    Fuente fuenteMock2 = mock(Fuente.class);

    Hecho hechoFuente1 = new HechoTestBuilder().conTitulo("Hecho 1").build();
    Hecho hechoFuente2 = new HechoTestBuilder().conTitulo("Hecho 2").build();
    Hecho hechoInactivo = new HechoTestBuilder().conTitulo("Inactivo").build();

    when(fuenteMock1.getHechos()).thenReturn(List.of(hechoFuente1));
    when(fuenteMock2.getHechos()).thenReturn(List.of(hechoFuente2, hechoInactivo));

    // Configuramos el mock para que solo los dos primeros hechos estén activos.
    when(mockRepoSolicitud.estaActivo(hechoFuente1)).thenReturn(true);
    when(mockRepoSolicitud.estaActivo(hechoFuente2)).thenReturn(true);
    when(mockRepoSolicitud.estaActivo(hechoInactivo)).thenReturn(false);

    // Act
    List<Hecho> todosLosHechos = navegador.obtenerTodosLosHechosActivos(List.of(fuenteMock1, fuenteMock2));

    // Assert
    assertEquals(2, todosLosHechos.size(), "Debe retornar la combinación de hechos activos de ambas fuentes.");
  }

  @Test
  public void navegarColeccion_NoDebeRetornarHechosQueNoPertenecen() {
    // Arrange
    // Este criterio solo acepta hechos con la categoría "Ciencia".
    CriterioDePertenencia criterioCiencia = new CriterioDePertenencia(List.of(new FiltroCategoria("Ciencia")));
    Coleccion coleccion = new Coleccion(
        "Colección de Ciencia", "Descripción",
        fuenteMock,
        criterioCiencia,
        ModoDeNavegacion.IRRESTRICTO);

    Hecho hechoDeCiencia = new HechoTestBuilder().conTitulo("Descubrimiento").conCategoria("Ciencia").build();
    Hecho hechoDeDeporte = new HechoTestBuilder().conTitulo("Gol").conCategoria("Deporte").build();

    // La fuente provee ambos hechos.
    when(fuenteMock.getHechos()).thenReturn(List.of(hechoDeCiencia, hechoDeDeporte));
    // Ambos hechos están activos.
    when(mockRepoSolicitud.estaActivo(any())).thenReturn(true);

    // Act
    List<Hecho> hechosNavegados = navegador.navegar(coleccion);

    // Assert
    assertEquals(1, hechosNavegados.size(), "Solo debe retornar el hecho que cumple el criterio de pertenencia.");
    assertEquals("Descubrimiento", hechosNavegados.get(0).getTitulo());
  }
}
