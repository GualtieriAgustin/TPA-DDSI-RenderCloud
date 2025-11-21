package ar.edu.utn.frba.dds.dominio.hechos.geo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BuscadorDeProvinciasTest {

  private static final String MOCK_GEOJSON_FILE_PATH = "mock_provincias.geojson";
  private static BuscadorDeProvincias buscador;

  @BeforeAll
  static void setup() {
    buscador = new BuscadorDeProvincias("geojson/provincias.geojson");
  }

  @Test
  void buscadorSeCargaCorrectamenteConArchivoValido() {
    assertNotNull(buscador);
  }

  @Test
  void buscarProvinciaPorCoordenadasDeCABA() {
    // Coordenadas que se encuentran dentro del polígono de CABA
    double latitud = -34.60;
    double longitud = -58.40;
    assertEquals(Provincia.CABA, buscador.buscarProvinciaPorCoordenadas(latitud, longitud));
  }

  @Test
  void buscarProvinciaPorCoordenadasDeBuenosAires() {
    // Coordenadas que se encuentran dentro del polígono de Buenos Aires
    double latitud = -34.50;
    double longitud = -58.50;
    assertEquals(Provincia.BUENOS_AIRES, buscador.buscarProvinciaPorCoordenadas(latitud, longitud));
  }

  @Test
  void buscarProvinciaPorCoordenadasDesconocidas() {
    // Coordenadas que no se encuentran dentro de ningún polígono de Argentina
    double latitud = -0.00;
    double longitud = -60.00;
    assertEquals(Provincia.PROVINCIA_DESCONOCIDA, buscador.buscarProvinciaPorCoordenadas(latitud, longitud));
  }

  @Test
  void constructorLanzaExcepcionSiElArchivoNoExiste() {
    assertThrows(RuntimeException.class, () -> new BuscadorDeProvincias("archivo_inexistente.geojson"));
  }
}