package ar.edu.utn.frba.dds.dominio.hechos.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class HechoOrigenNoCreadoPorUsuarioExceptionTest {
  @Test
  void constructor_CuandoSeCrea_DebeTenerMensajeCorrecto() {
    HechoOrigenNoCreadoPorUsuarioException exception = new HechoOrigenNoCreadoPorUsuarioException();
    assertEquals(
        "El hecho debe tener origen CONTRIBUYENTE para ser creado por un usuario.",
        exception.getMessage()
    );
  }

  @Test
  void constructor_CuandoSeCrea_DebeSerRuntimeException() {
    HechoOrigenNoCreadoPorUsuarioException exception = new HechoOrigenNoCreadoPorUsuarioException();
    assertTrue(exception instanceof RuntimeException);
  }
}
