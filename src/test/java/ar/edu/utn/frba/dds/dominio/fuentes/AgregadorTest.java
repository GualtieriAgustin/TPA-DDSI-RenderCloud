package ar.edu.utn.frba.dds.dominio.fuentes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import ar.edu.utn.frba.dds.utils.factories.FuenteMockFactory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AgregadorTest {

  private FuenteMockFactory fuenteFactory;

  @BeforeEach
  public void setUp() {
    fuenteFactory = new FuenteMockFactory();
  }

  @Test
  public void getHechos_CuandoNoHayFuentes_DeberiaRetornarListaVacia() {
    // Arrange
    Agregador agregador = new Agregador();

    // Act
    List<Hecho> hechosCombinados = agregador.getHechos();

    // Assert
    assertTrue(hechosCombinados.isEmpty());
  }

  @Test
  public void getHechos_CuandoHayUnaFuente_DeberiaRetornarHechosDeEsaFuente() {
    // Arrange
    Hecho hecho = new HechoTestBuilder().build();
    Fuente fuente = fuenteFactory.create((List.of(hecho)));

    Agregador agregador = new Agregador();
    agregador.agregarFuente(fuente);

    // Act
    List<Hecho> hechosCombinados = agregador.getHechos();

    // Assert
    assertFalse(hechosCombinados.isEmpty());
    assertEquals(hecho, hechosCombinados.get(0));
  }

  @Test
  public void getHechos_CuandoHayVariasFuentes_DeberiaCombinarHechosDeTodasLasFuentes() {
    // Arrange
    Hecho hecho1 = new HechoTestBuilder()
        .conTitulo("Incendio")
        .conDescripcion("Incendio en un edificio")
        .conCategoria("Accidente")
        .build();

    Hecho hecho2 = new HechoTestBuilder()
        .conTitulo("Inundación")
        .conDescripcion("Inundación en la ciudad")
        .conCategoria("Desastre natural")
        .build();

    Fuente fuente1 = fuenteFactory.create(List.of(hecho1));
    Fuente fuente2 = fuenteFactory.create(List.of(hecho2));

    Agregador agregador = new Agregador();
    agregador.agregarFuente(fuente1);
    agregador.agregarFuente(fuente2);

    // Act
    List<Hecho> hechosCombinados = agregador.getHechos();

    // Assert
    assertEquals(2, hechosCombinados.size());
  }

  @Test
  public void getHechos_CuandoHayHechosDuplicadosEnUnaFuente_DeberiaEliminarDuplicados() {
    // Arrange
    Hecho hecho1 = new HechoTestBuilder()
        .conTitulo("Incendio")
        .conDescripcion("Incendio en un edificio")
        .build();

    Hecho hechoDuplicado = new HechoTestBuilder()
        .conTitulo("Incendio")
        .conDescripcion("Otro incendio en un edificio")
        .build();

    Fuente fuente = fuenteFactory.create(List.of(hecho1, hechoDuplicado));

    Agregador agregador = new Agregador();
    agregador.agregarFuente(fuente);

    // Act
    List<Hecho> hechosCombinados = agregador.getHechos();

    // Assert
    assertFalse(hechosCombinados.isEmpty());
    assertEquals("Incendio", hechosCombinados.get(0).getTitulo());
    assertEquals("Otro incendio en un edificio", hechosCombinados.get(0).getDescripcion());
  }
}
