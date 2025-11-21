package ar.edu.utn.frba.dds.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.hechos.geo.BuscadorDeProvincias;
import io.javalin.http.Context;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProvinciasControllerTest {

  private ProvinciasController provinciasController;
  private BuscadorDeProvincias buscadorDeProvinciasMock;
  private Context ctxMock;

  @BeforeEach
  void setUp() {
    buscadorDeProvinciasMock = mock(BuscadorDeProvincias.class);
    provinciasController = new ProvinciasController(buscadorDeProvinciasMock);
    ctxMock = mock(Context.class);
  }

  @Test
  void listarDevuelveTodasLasProvincias() {
    List<Map<String, String>> provincias = provinciasController.listar();

    assertNotNull(provincias);
    assertEquals(Provincia.values().length, provincias.size());

    // Verificamos que una provincia específica (ej. CABA) esté bien formada
    Map<String, String> caba = provincias.stream()
        .filter(p -> p.get("value").equals("CABA"))
        .findFirst()
        .orElse(null);

    assertNotNull(caba);
    assertEquals("CABA", caba.get("value"));
    assertEquals("Ciudad Autónoma de Buenos Aires", caba.get("nombre"));
  }

  @Test
  void buscarProvinciaPorCoordenadasEncontrada() {
    double lat = -34.6037;
    double lon = -58.3816;

    when(ctxMock.queryParam(eq("lat"))).thenReturn(String.valueOf(lat));
    when(ctxMock.queryParam(eq("lon"))).thenReturn(String.valueOf(lon));
    when(buscadorDeProvinciasMock.buscarProvinciaPorCoordenadas(lat, lon)).thenReturn(Provincia.CABA);

    Map<String, String> resultado = provinciasController.buscarProvincia(ctxMock);

    assertEquals("CABA", resultado.get("value"));
    assertEquals("Ciudad Autónoma de Buenos Aires", resultado.get("nombre"));
  }

  @Test
  void buscarProvinciaSinParametrosLanzaExcepcion() {
    // Simulamos que el parámetro "lat" no viene en la petición
    when(ctxMock.queryParam("lat")).thenReturn(null);
    when(ctxMock.queryParam("lon")).thenReturn("-58.3816");

    // Verificamos que se lanza la excepción esperada debido al Objects.requireNonNull()
    assertThrows(NullPointerException.class, () -> {
      provinciasController.buscarProvincia(ctxMock);
    });
  }
}