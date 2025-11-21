package ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class HechoResponseTest {
  @Test
  void toDomain_CuandoSeConvierte_DeberiaRetornarHechoConDatosCorrectos() {
    // Arrange
    String titulo = "Título de prueba";
    String descripcion = "Descripción de prueba";
    String categoria = "Categoría de prueba";
    Ubicacion ubicacion = new Ubicacion(10.0, 20.0);
    LocalDateTime fechaSuceso = LocalDateTime.of(2023, 10, 1, 12, 0);
    LocalDateTime fechaCarga = LocalDateTime.of(2023, 10, 2, 14, 0);

    HechoResponse hechoResponse = new HechoResponse(
        titulo, descripcion, categoria, ubicacion, fechaSuceso, fechaCarga, null
    );

    // Act
    Hecho hecho = hechoResponse.toDomain();

    // Assert
    assertEquals(titulo, hecho.getTitulo());
    assertEquals(descripcion, hecho.getDescripcion());
    assertEquals(categoria, hecho.getCategoria());
    assertEquals(ubicacion, hecho.getUbicacion());
    assertEquals(fechaSuceso, hecho.getFechaSuceso());
    assertEquals(fechaCarga, hecho.getFechaCarga());
  }

}
