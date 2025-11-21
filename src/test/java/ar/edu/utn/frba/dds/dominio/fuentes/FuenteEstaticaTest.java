package ar.edu.utn.frba.dds.dominio.fuentes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import ar.edu.utn.frba.dds.dominio.archivos.LectorArchivo;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.utils.factories.FuenteMockFactory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class FuenteEstaticaTest {
  private FuenteMockFactory fuenteFactory;

  @BeforeEach
  public void setUp() {
    fuenteFactory = new FuenteMockFactory();
  }

  @Test
  public void getHechos_CuandoSeInicializaConHechos_DeberiaRetornarEsosHechos() {
    // Arrange
    Hecho hecho1 = new HechoTestBuilder()
        .conTitulo("Incendio")
        .conDescripcion("Incendio en un edificio")
        .build();

    Hecho hecho2 = new HechoTestBuilder()
        .conTitulo("Inundación")
        .conDescripcion("Inundación en la ciudad")
        .build();

    FuenteEstatica fuente = fuenteFactory.create(List.of(hecho1, hecho2));

    // Act
    List<Hecho> hechos = fuente.getHechos();

    // Assert
    assertEquals(2, hechos.size());
  }

  @Test
  public void getOrigen_CuandoSeInicializa_DeberiaRetornarElDetalleOrigenCorrecto() {
    // Arrange
    var nombreArchivo = "incendios.csv";
    LectorArchivo lectorMock = Mockito.mock(LectorArchivo.class);
    when(lectorMock.leerHechos()).thenReturn(List.of());
    when(lectorMock.getFileName()).thenReturn(nombreArchivo);


    FuenteEstatica fuente = new FuenteEstatica(lectorMock);

    // Act
    String origen = fuente.getDetalleOrigen();

    // Assert
    assertEquals(nombreArchivo, origen);
  }

  @Test
  public void getTipo_CuandoSeInicializa_DeberiaRetornarTipoDataset() {
    // Arrange
    FuenteEstatica fuente = fuenteFactory.create(List.of());

    // Act
    Origen tipo = fuente.getOrigen();

    // Assert
    assertEquals(Origen.DATASET, tipo);
  }

  @Test
  public void getHechos_CuandoHayHechosDuplicados_DeberiaEliminarDuplicados() {
    // Arrange
    Hecho hecho1 = new HechoTestBuilder()
        .conTitulo("Incendio")
        .conDescripcion("Incendio en un edificio")
        .build();

    Hecho hechoDuplicado = new HechoTestBuilder()
        .conTitulo("Incendio")
        .conDescripcion("Otro incendio en un edificio")
        .build();

    FuenteEstatica fuente = fuenteFactory.create(List.of(hecho1, hechoDuplicado));

    // Act
    List<Hecho> hechos = fuente.getHechos();

    // Assert
    assertEquals(1, hechos.size());
    assertEquals("Incendio", hechos.get(0).getTitulo());
    assertEquals("Otro incendio en un edificio", hechos.get(0).getDescripcion());
  }

}
