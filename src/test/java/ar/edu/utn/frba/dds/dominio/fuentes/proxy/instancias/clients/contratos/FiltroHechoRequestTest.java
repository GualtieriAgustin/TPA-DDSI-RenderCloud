package ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class FiltroHechoRequestTest {

  @Test
  void getCategoria_CuandoSeCreaSinDatos_DeberiaRetornarNull() {
    // Arrange
    // Act
    FiltroHechoRequest filtro = new FiltroHechoRequest();

    // Assert
    assertNull(filtro.getCategoria());
    assertNull(filtro.getFechaAcontecimientoDesde());
    assertNull(filtro.getFechaAcontecimientoHasta());
    assertNull(filtro.getFechaReporteDesde());
    assertNull(filtro.getFechaReporteHasta());
    assertNull(filtro.getUbicacion());
  }

  @Test
  void getCategoria_CuandoTieneValores_DeberiaRetornarValor() {
    // Arrange
    String categoria = "categoria";
    LocalDateTime fechaReporteDesde = LocalDateTime.of(2023, 10, 1, 12, 0);
    LocalDateTime fechaReporteHasta = LocalDateTime.of(2023, 10, 2, 12, 0);
    LocalDateTime fechaAcontecimientoDesde = LocalDateTime.of(2023, 9, 30, 12, 0);
    LocalDateTime fechaAcontecimientoHasta = LocalDateTime.of(2023, 10, 1, 12, 0);
    Ubicacion ubicacion = new Ubicacion(10.0, 20.0);

    // Act
    FiltroHechoRequest filtro = new FiltroHechoRequest()
        .conCategoria(categoria)
        .conFechaAcontecimientoDesde(fechaAcontecimientoDesde)
        .conFechaAcontecimientoHasta(fechaAcontecimientoHasta)
        .conFechaReporteDesde(fechaReporteDesde)
        .conFechaReporteHasta(fechaReporteHasta)
        .conUbicacion(ubicacion)
        .create();

    // Assert
    assertEquals(categoria, filtro.getCategoria());
    assertEquals("2023-10-01T12:00", filtro.getFechaReporteDesde());
    assertEquals("2023-10-02T12:00", filtro.getFechaReporteHasta());
    assertEquals("2023-09-30T12:00", filtro.getFechaAcontecimientoDesde());
    assertEquals("2023-10-01T12:00", filtro.getFechaAcontecimientoHasta());
    assertEquals("10.0,20.0", filtro.getUbicacion());
  }
}
