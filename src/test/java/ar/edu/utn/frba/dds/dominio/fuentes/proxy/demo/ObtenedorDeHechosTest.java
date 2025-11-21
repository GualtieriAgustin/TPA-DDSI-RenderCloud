package ar.edu.utn.frba.dds.dominio.fuentes.proxy.demo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ObtenedorDeHechosTest {

  private final String testUrl = "https://fuentesproxy.com/fuenteDemo";
  private final LocalDateTime fechaConsulta = LocalDateTime.now();
  private Conexion mockConexion;

  @BeforeEach
  void setUp() {
    mockConexion = mock(Conexion.class);
  }

  @Test
  void getNuevosHechos_cuandoConexionNoDevuelveNuevosHechos() {
    when(mockConexion.siguienteHecho(any(), any())).thenReturn(null);
    ObtenedorDeHechos obtenedorDeHechos = new ObtenedorDeHechos(testUrl, mockConexion);

    List<Hecho> hechos = obtenedorDeHechos.getNuevosHechos(fechaConsulta);

    assertTrue(hechos.isEmpty(), "La lista de hechos debería estar vacía.");
    verify(mockConexion, times(1)).siguienteHecho(any(), any());
  }

  @Test
  void getNuevosHechos_cuandoConexionDevuelveUnHecho() {
    LocalDateTime fechaSucesoHecho1 = LocalDateTime.of(2025, 5, 15, 12, 0, 0);
    Map<String, Object> mapaHecho1 = crearMapaHecho("Hecho 1", "descripcion", fechaSucesoHecho1);

    // devuelve un hecho, luego null
    when(mockConexion.siguienteHecho(any(), eq(fechaConsulta)))
        .thenReturn(mapaHecho1)
        .thenReturn(null);
    ObtenedorDeHechos obtenedorDeHechos = new ObtenedorDeHechos(testUrl, mockConexion);

    List<Hecho> hechos = obtenedorDeHechos.getNuevosHechos(fechaConsulta);

    assertEquals(1, hechos.size(), "Debería haber un hecho.");
    assertEquals("Hecho 1", hechos.get(0).getTitulo());

    verify(mockConexion, times(2)).siguienteHecho(any(), eq(fechaConsulta));
  }

  @Test
  void getNuevosHechos_cuandoConexionDevuelveVariosHechos() {
    LocalDateTime fechaSucesoHecho1 = LocalDateTime.of(2025, 5, 15, 12, 0, 0);
    LocalDateTime fechaSucesoHecho2 = LocalDateTime.of(2025, 5, 16, 14, 0, 0);
    Map<String, Object> mapaHecho1 = crearMapaHecho("Hecho 1", "Desc 1", fechaSucesoHecho1);
    Map<String, Object> mapaHecho2 = crearMapaHecho("Hecho 2", "Desc 2", fechaSucesoHecho2);
    when(mockConexion.siguienteHecho(any(), any()))
        .thenReturn(mapaHecho1)
        .thenReturn(mapaHecho2)
        .thenReturn(null);
    ObtenedorDeHechos obtenedorDeHechos = new ObtenedorDeHechos(testUrl, mockConexion);

    List<Hecho> hechos = obtenedorDeHechos.getNuevosHechos(fechaConsulta);

    assertEquals(2, hechos.size());
    assertEquals("Hecho 1", hechos.get(0).getTitulo());
    assertEquals("Hecho 2", hechos.get(1).getTitulo());

    verify(mockConexion, times(3)).siguienteHecho(any(), any());
  }


  @Test()
  void getNuevosHechos_cuandoConexionAtrapaExcepcion() {
    when(mockConexion.siguienteHecho(any(), any()))
        .thenThrow(new RuntimeException("Error de conexión simulado"));

    ObtenedorDeHechos obtenedorDeHechos = new ObtenedorDeHechos(testUrl, mockConexion);
    assertDoesNotThrow(() -> {
      List<Hecho> hechos = obtenedorDeHechos.getNuevosHechos(fechaConsulta);
      assertTrue(hechos.isEmpty(), "No deberían agregarse hechos si hay una excepción.");
    });

    verify(mockConexion, times(1)).siguienteHecho(any(), any());
  }

  private Map<String, Object> crearMapaHecho(String titulo, String descripcion, LocalDateTime fechaSuceso) {
    Map<String, Object> map = new HashMap<>();
    map.put("titulo", titulo);
    map.put("descripcion", descripcion);
    map.put("categoria", "TestCategoria");
    map.put("latitud", -34.0);
    map.put("longitud", -58.0);
    map.put("fechaSuceso", fechaSuceso);
    return map;
  }
}