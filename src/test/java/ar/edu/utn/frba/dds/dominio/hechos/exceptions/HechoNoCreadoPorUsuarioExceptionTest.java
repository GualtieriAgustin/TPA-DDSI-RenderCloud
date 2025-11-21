import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.utn.frba.dds.dominio.hechos.exceptions.HechoNoCreadoPorUsuarioException;
import org.junit.jupiter.api.Test;

class HechoNoCreadoPorUsuarioExceptionTest {

  @Test
  void constructor_CuandoSeCrea_DebeTenerMensajeCorrecto() {
    HechoNoCreadoPorUsuarioException exception = new HechoNoCreadoPorUsuarioException();
    assertEquals("El hecho debe ser un credo por usuario.", exception.getMessage());
  }

  @Test
  void constructor_CuandoSeCrea_DebeSerRuntimeException() {
    HechoNoCreadoPorUsuarioException exception = new HechoNoCreadoPorUsuarioException();
    assertTrue(exception instanceof RuntimeException);
  }
}