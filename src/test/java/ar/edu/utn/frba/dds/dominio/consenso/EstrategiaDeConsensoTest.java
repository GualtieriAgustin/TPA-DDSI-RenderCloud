package ar.edu.utn.frba.dds.dominio.consenso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ar.edu.utn.frba.dds.dominio.consenso.estrategias.AbsolutaEstrategia;
import ar.edu.utn.frba.dds.dominio.consenso.estrategias.MayoriaSimpleEstrategia;
import ar.edu.utn.frba.dds.dominio.consenso.estrategias.MultiplesMencionesEstrategia;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EstrategiaDeConsensoTest {

  private Hecho unHechoCualquiera;
  private EstrategiaDeConsenso estrategia;
  private DatosGlobalesDeConsenso datosMock;

  @BeforeEach
  void setUp() {
    this.unHechoCualquiera = new HechoTestBuilder().build();
    this.datosMock = mock(DatosGlobalesDeConsenso.class);
  }

  @Test
  @DisplayName("Logra consenso si las menciones superan el 50%")
  void determinarConsenso_conMasDeLaMitadDeMenciones_esConsensuado() {
    estrategia = new MayoriaSimpleEstrategia();
    // ARRANGE: Simulamos los datos de consenso con un mock.
    // Escenario: 3 menciones de 5 fuentes totales (60%) y sin conflictos.
    when(datosMock.mencionesDelHecho()).thenReturn(3L);
    when(datosMock.totalDeFuentes()).thenReturn(5);
    when(datosMock.existeConflicto()).thenReturn(false);

    // ACT: Ejecutamos la estrategia con los datos simulados.
    NivelDeConsenso resultado = estrategia.calcular(unHechoCualquiera, datosMock);

    // ASSERT: Verificamos que el resultado es el esperado.
    assertEquals(NivelDeConsenso.CONSENSUADO, resultado);
  }

  @Test
  @DisplayName("No logra consenso si las menciones no superan el 50%")
  void determinarConsenso_conLaMitadOMenosDeMenciones_noEsConsensuado() {
    estrategia = new MayoriaSimpleEstrategia();
    // ARRANGE: Escenario: 2 menciones de 5 fuentes totales (40%) y sin conflictos.
    when(datosMock.mencionesDelHecho()).thenReturn(2L);
    when(datosMock.totalDeFuentes()).thenReturn(5);
    when(datosMock.existeConflicto()).thenReturn(false);

    // ACT
    NivelDeConsenso resultado = estrategia.calcular(unHechoCualquiera, datosMock);

    // ASSERT
    assertEquals(NivelDeConsenso.NO_CONSENSUADO, resultado);
  }

  @Test
  @DisplayName("No logra consenso si existe un conflicto, sin importar las menciones")
  void determinarConsenso_conConflicto_noEsConsensuado() {
    estrategia = new MultiplesMencionesEstrategia();
    // ARRANGE: Escenario: 5 menciones de 5 (100%), pero con un conflicto.
    when(datosMock.mencionesDelHecho()).thenReturn(5L);
    when(datosMock.totalDeFuentes()).thenReturn(5);
    when(datosMock.existeConflicto()).thenReturn(true); // <-- ¡La condición clave!

    // ACT
    NivelDeConsenso resultado = estrategia.calcular(unHechoCualquiera, datosMock);

    // ASSERT: El conflicto debe anular el consenso.
    assertEquals(NivelDeConsenso.NO_CONSENSUADO, resultado);
  }

  @Test
  @DisplayName("Logra consenso si todas las fuentes lo mencionan")
  void determinarConsenso_conTodasLasMenciones_esConsensuado() {
    estrategia = new AbsolutaEstrategia();
    // ARRANGE: Escenario: 5 menciones de 5 fuentes totales (100%) y sin conflictos.
    when(datosMock.mencionesDelHecho()).thenReturn(5L);
    when(datosMock.totalDeFuentes()).thenReturn(5);
    when(datosMock.existeConflicto()).thenReturn(false);

    // ACT
    NivelDeConsenso resultado = estrategia.calcular(unHechoCualquiera, datosMock);

    // ASSERT
    assertEquals(NivelDeConsenso.CONSENSUADO, resultado);
  }

  @Test
  @DisplayName("No logra consenso si falta al menos una mención")
  void determinarConsenso_conMenosDeTodasLasMenciones_noEsConsensuado() {
    estrategia = new AbsolutaEstrategia();
    // ARRANGE: Escenario: 4 menciones de 5 fuentes totales (80%) y sin conflictos.
    when(datosMock.mencionesDelHecho()).thenReturn(4L);
    when(datosMock.totalDeFuentes()).thenReturn(5);
    when(datosMock.existeConflicto()).thenReturn(false);

    // ACT
    NivelDeConsenso resultado = estrategia.calcular(unHechoCualquiera, datosMock);

    // ASSERT
    assertEquals(NivelDeConsenso.NO_CONSENSUADO, resultado);
  }

  @Test
  @DisplayName("No logra consenso si existe un conflicto, aunque todas las fuentes lo mencionen")
  void determinarConsenso_conConflicto_conMencionesEnTodasLasFuentes_noEsConsensuado() {
    estrategia = new MultiplesMencionesEstrategia();
    // ARRANGE: Escenario: 5 menciones de 5 (100%), pero con un conflicto.
    when(datosMock.mencionesDelHecho()).thenReturn(5L);
    when(datosMock.totalDeFuentes()).thenReturn(5);
    when(datosMock.existeConflicto()).thenReturn(true); // <-- ¡La condición clave!

    // ACT
    NivelDeConsenso resultado = estrategia.calcular(unHechoCualquiera, datosMock);

    // ASSERT: El conflicto debe anular el consenso.
    assertEquals(NivelDeConsenso.NO_CONSENSUADO, resultado);
  }

}