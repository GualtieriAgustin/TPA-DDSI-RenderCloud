package ar.edu.utn.frba.dds.dominio.hechos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UbicacionTest {

  @Test
  public void constructor_DebeSetearCorrectamenteLasPropiedades() {
    // Arrange
    Double latitud = 10.0;
    Double longitud = 20.0;

    // Act
    Ubicacion ubicacion = new Ubicacion(latitud, longitud);

    // Assert
    assertEquals(latitud, ubicacion.getLatitud());
    assertEquals(longitud, ubicacion.getLongitud());
  }
}
