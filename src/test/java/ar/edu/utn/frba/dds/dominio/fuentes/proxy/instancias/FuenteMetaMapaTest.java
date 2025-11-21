package ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroCategoria;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FuenteMetaMapaTest {
  private ServicioMetamapa servicioMock1;
  private ServicioMetamapa servicioMock2;
  private FuenteMetaMapa fuenteMetaMapa;

  @BeforeEach
  void setUp() {
    servicioMock1 = mock(ServicioMetamapa.class);
    servicioMock2 = mock(ServicioMetamapa.class);
    fuenteMetaMapa = new FuenteMetaMapa(List.of(servicioMock1, servicioMock2));
  }

  @Test
  void getHechos_CuandoUnServiciosRetornanHechos_DeberiaTenerHechos() {
    // Arrange
    fuenteMetaMapa = new FuenteMetaMapa(List.of(servicioMock1));

    CriterioDePertenencia criterio = new CriterioDePertenencia(new FiltroCategoria("incendio"));

    Hecho hecho1 = new HechoTestBuilder()
        .conTitulo("Titulo1")
        .conDescripcion("Descripcion1")
        .conCategoria("incendio")
        .build();

    when(servicioMock1.getHechos(criterio)).thenReturn(List.of(hecho1));

    // Act
    List<Hecho> hechos = fuenteMetaMapa.getHechosPorCriterio(criterio);

    // Assert
    assertEquals(1, hechos.size());
    assertTrue(hechos.contains(hecho1));
  }

  @Test
  void getHechos_CuandoServiciosRetornanHechos_DeberiaCombinarHechos() {
    // Arrange
    CriterioDePertenencia criterio = new CriterioDePertenencia(new FiltroCategoria("incendio"));

    Hecho hecho1 = new HechoTestBuilder()
        .conTitulo("Titulo1")
        .conDescripcion("Descripcion1")
        .conCategoria("incendio")
        .build();

    Hecho hecho2 = new HechoTestBuilder()
        .conTitulo("Titulo2")
        .conDescripcion("Descripcion2")
        .conCategoria("incendio")
        .build();

    when(servicioMock1.getHechos(criterio)).thenReturn(List.of(hecho1));
    when(servicioMock2.getHechos(criterio)).thenReturn(List.of(hecho2));

    // Act
    List<Hecho> hechos = fuenteMetaMapa.getHechosPorCriterio(criterio);

    // Assert
    assertEquals(2, hechos.size());
    assertTrue(hechos.contains(hecho1));
    assertTrue(hechos.contains(hecho2));
  }

  @Test
  void getHechos_CuandoServiciosRetornanListasVacias_DeberiaRetornarListaVaciaDeHechos() {
    // Arrange
    CriterioDePertenencia criterio = new CriterioDePertenencia(new FiltroCategoria("inundacion"));

    when(servicioMock1.getHechos(criterio)).thenReturn(List.of());
    when(servicioMock2.getHechos(criterio)).thenReturn(List.of());

    // Act
    List<Hecho> hechos = fuenteMetaMapa.getHechosPorCriterio(criterio);

    // Assert
    assertTrue(hechos.isEmpty());
  }

  @Test
  void getHechosSinFiltros_CuandoServiciosRetornanHechos_DeberiaCombinarHechos() {
    Hecho hecho1 = new HechoTestBuilder().conTitulo("h1").build();
    Hecho hecho2 = new HechoTestBuilder().conTitulo("h2").build();

    when(servicioMock1.getHechos(any(CriterioDePertenencia.class))).thenReturn(List.of(hecho1));
    when(servicioMock2.getHechos(any(CriterioDePertenencia.class))).thenReturn(List.of(hecho2));

    List<Hecho> hechos = fuenteMetaMapa.getHechos();

    assertEquals(2, hechos.size());
    assertTrue(hechos.contains(hecho1));
    assertTrue(hechos.contains(hecho2));
  }
}
