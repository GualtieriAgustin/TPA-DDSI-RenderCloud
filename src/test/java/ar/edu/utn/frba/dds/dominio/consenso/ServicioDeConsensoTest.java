package ar.edu.utn.frba.dds.dominio.consenso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ar.edu.utn.frba.dds.dominio.colecciones.Coleccion;
import ar.edu.utn.frba.dds.dominio.colecciones.ModoDeNavegacion;
import ar.edu.utn.frba.dds.dominio.colecciones.NavegadorDeColecciones;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.fuentes.Fuente;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitud;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ServicioDeConsensoTest {
  private ServicioDeConsenso servicioDeConsenso;

  private NavegadorDeColecciones navegador;
  private Fuente fuenteAMock;
  private Fuente fuenteBMock;
  private Fuente fuenteCMock;
  private Fuente fuenteDMock;

  private List<Coleccion> colecciones;
  private List<Fuente> fuentes;

  private Hecho hechoConsensuado;
  private Hecho hechoEnConflicto1;
  private Hecho hechoEnConflicto2;
  private Hecho hechoSinConsenso;
  private Hecho hechoInactivo;

  @BeforeEach
  void setUp() {
    RepositorioSolicitud repositorioSolicitudesMock = mock(RepositorioSolicitud.class);
    this.fuenteAMock = mock(Fuente.class);
    this.fuenteBMock = mock(Fuente.class);
    this.fuenteCMock = mock(Fuente.class);
    this.fuenteDMock = mock(Fuente.class);

    this.navegador = new NavegadorDeColecciones(repositorioSolicitudesMock);
    this.servicioDeConsenso = new ServicioDeConsenso(this.navegador);

    this.fuentes = new ArrayList<>();
    this.colecciones = new ArrayList<>();
    this.hechoConsensuado = new HechoTestBuilder().conTitulo("Incendio").build();
    this.hechoSinConsenso = new HechoTestBuilder().conTitulo("Demoras").build();
    this.hechoInactivo = new HechoTestBuilder().conTitulo("Corte de luz").build();
    this.hechoEnConflicto1 = new HechoTestBuilder().conTitulo("Accidente").conDescripcion("Camión").build();
    this.hechoEnConflicto2 = new HechoTestBuilder().conTitulo("Accidente").conDescripcion("Autos").build();

    // Comportamiento general de los mocks
    when(repositorioSolicitudesMock.estaActivo(any(Hecho.class))).thenReturn(true);
    when(repositorioSolicitudesMock.estaActivo(hechoInactivo)).thenReturn(false);
  }

  @Test
  @DisplayName("Con MULTIPLES_MENCIONES, un hecho mencionado 2 veces es consensuado")
  void ponderarConsensoGlobal_multiplesMenciones() {
    // ARRANGE
    when(fuenteAMock.getHechos()).thenReturn(List.of(hechoEnConflicto1));
    when(fuenteBMock.getHechos()).thenReturn(List.of(hechoEnConflicto1));
    when(fuenteCMock.getHechos()).thenReturn(List.of(hechoSinConsenso));

    this.fuentes.addAll(List.of(fuenteAMock, fuenteBMock, fuenteCMock));

    Coleccion coleccion = new Coleccion(
        "Noticias de Emergencia",
        "Colección para hechos de emergencia",
        fuenteAMock,
        new CriterioDePertenencia(), // Debe ser no-nulo para que `navegar` no falle.
        ModoDeNavegacion.CURADO);

    coleccion.setAlgoritmoDeConsenso(AlgoritmoDeConsenso.MULTIPLES_MENCIONES);
    this.colecciones.add(coleccion);

    // ACT
    this.servicioDeConsenso.ponderarConsensoGlobal(this.fuentes, this.colecciones);

    // ASSERT
    List<Hecho> hechosVisibles = navegador.navegar(coleccion, Collections.emptyList());
    // hechoEnConflicto1 aparece en 2 de 3 fuentes, sin conflicto, por lo tanto es visible.
    assertEquals(1, hechosVisibles.size());
    assertEquals(this.hechoEnConflicto1, hechosVisibles.get(0));
  }

  @Test
  @DisplayName("Con MULTIPLES_MENCIONES, un hecho con conflicto no es consensuado")
  void ponderarConsensoGlobal_multiplesMenciones_ConConflictos() {
    // ARRANGE
    when(fuenteAMock.getHechos()).thenReturn(List.of(hechoEnConflicto1));
    when(fuenteBMock.getHechos()).thenReturn(List.of(hechoEnConflicto1));
    when(fuenteCMock.getHechos()).thenReturn(List.of(hechoSinConsenso));
    when(fuenteDMock.getHechos()).thenReturn(List.of(hechoEnConflicto2));

    this.fuentes.addAll(List.of(fuenteAMock, fuenteBMock, fuenteCMock, fuenteDMock));

    Coleccion coleccion = new Coleccion(
        "Noticias de Emergencia",
        "Colección para hechos de emergencia",
        fuenteAMock,
        new CriterioDePertenencia(),
        ModoDeNavegacion.CURADO);

    coleccion.setAlgoritmoDeConsenso(AlgoritmoDeConsenso.MULTIPLES_MENCIONES);
    this.colecciones.add(coleccion);

    // ACT
    this.servicioDeConsenso.ponderarConsensoGlobal(this.fuentes, this.colecciones);

    // ASSERT
    List<Hecho> hechosVisibles = navegador.navegar(coleccion, Collections.emptyList());
    // hechoEnConflicto1 aparece en 2 fuentes, pero hechoEnConflicto2 (mismo título) genera conflicto.
    assertTrue(hechosVisibles.isEmpty());
  }

  @Test
  @DisplayName("Con MAYORIA_SIMPLE, un hecho mencionado en 2 de 4 fuentes es consensuado")
  void ponderarConsensoGlobal_mayoriaSimple() {
    // ARRANGE
    when(fuenteAMock.getHechos()).thenReturn(List.of(hechoConsensuado));
    when(fuenteBMock.getHechos()).thenReturn(List.of(hechoConsensuado));
    when(fuenteCMock.getHechos()).thenReturn(List.of(hechoSinConsenso));
    when(fuenteDMock.getHechos()).thenReturn(List.of(hechoSinConsenso));

    this.fuentes.addAll(List.of(fuenteAMock, fuenteBMock, fuenteCMock, fuenteDMock));

    Coleccion coleccion = new Coleccion(
        "Noticias de Emergencia",
        "Colección para hechos de emergencia",
        fuenteAMock,
        new CriterioDePertenencia(),
        ModoDeNavegacion.CURADO);

    coleccion.setAlgoritmoDeConsenso(AlgoritmoDeConsenso.MAYORIA_SIMPLE);
    this.colecciones.add(coleccion);

    // ACT
    this.servicioDeConsenso.ponderarConsensoGlobal(this.fuentes, this.colecciones);

    // ASSERT
    List<Hecho> hechosVisibles = navegador.navegar(coleccion, Collections.emptyList());
    // hechoConsensuado aparece en 2/4 fuentes (50%), por lo tanto es visible.
    assertEquals(1, hechosVisibles.size());
    assertEquals(this.hechoConsensuado, hechosVisibles.get(0));
  }

  @Test
  @DisplayName("Con ABSOLUTA, un hecho debe aparecer en todas las fuentes para ser consensuado")
  void ponderarConsensoGlobal_absoluta() {
    // ARRANGE
    when(fuenteAMock.getHechos()).thenReturn(List.of(hechoConsensuado, hechoEnConflicto1));
    when(fuenteBMock.getHechos()).thenReturn(List.of(hechoConsensuado, hechoEnConflicto2));
    when(fuenteCMock.getHechos()).thenReturn(List.of(hechoConsensuado, hechoSinConsenso));
    when(fuenteDMock.getHechos()).thenReturn(List.of(hechoConsensuado, hechoInactivo));
    this.fuentes.addAll(List.of(fuenteAMock, fuenteBMock, fuenteCMock, fuenteDMock));

    Coleccion coleccion = new Coleccion(
        "Colección de prueba con Mock",
        "Descripción",
        fuenteAMock,
        new CriterioDePertenencia(),
        ModoDeNavegacion.CURADO);

    coleccion.setAlgoritmoDeConsenso(AlgoritmoDeConsenso.ABSOLUTA);
    this.colecciones.add(coleccion);

    // ACT
    this.servicioDeConsenso.ponderarConsensoGlobal(this.fuentes, this.colecciones);

    // ASSERT
    List<Hecho> hechosVisibles = navegador.navegar(coleccion, Collections.emptyList());
    // Solo hechoConsensuado aparece en las 4 fuentes.
    assertEquals(1, hechosVisibles.size());
    assertEquals(this.hechoConsensuado, hechosVisibles.get(0));
  }

  @Test
  @DisplayName("Con MULTIPLES_MENCIONES, un hecho con conflicto no es consensuado")
  void ponderarConsensoGlobal_conConflictoDeHechos() {
    // ARRANGE
    when(fuenteAMock.getHechos()).thenReturn(List.of(this.hechoEnConflicto1));
    when(fuenteBMock.getHechos()).thenReturn(List.of(this.hechoEnConflicto2));
    this.fuentes.addAll(List.of(fuenteAMock, fuenteBMock));

    Coleccion coleccion = new Coleccion(
        "Colección de prueba con Conflicto",
        "Descripción",
        fuenteAMock,
        new CriterioDePertenencia(),
        ModoDeNavegacion.CURADO);

    coleccion.setAlgoritmoDeConsenso(AlgoritmoDeConsenso.MULTIPLES_MENCIONES);
    this.colecciones.add(coleccion);

    // ACT
    this.servicioDeConsenso.ponderarConsensoGlobal(this.fuentes, this.colecciones);

    // ASSERT
    List<Hecho> hechosVisibles = navegador.navegar(coleccion, Collections.emptyList());
    // Los hechos tienen el mismo título pero distinta descripción, generando un conflicto.
    assertTrue(hechosVisibles.isEmpty());
  }
}