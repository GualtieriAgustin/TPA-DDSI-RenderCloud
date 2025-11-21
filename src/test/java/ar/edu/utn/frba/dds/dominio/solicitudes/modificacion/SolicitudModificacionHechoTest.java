package ar.edu.utn.frba.dds.dominio.solicitudes.modificacion;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SolicitudModificacionHechoTest {

  private SolicitudModificacionHecho solicitud;

  @BeforeEach
  void setUp() {
    Usuario homero = new Usuario("homero", true, 1L);
    Hecho hecho = new HechoTestBuilder().conUsuario(homero).build();
    this.solicitud = new SolicitudModificacionHecho(
        123L,
        "justificacion",
        homero,
        new ModificacionHecho()
    );
  }

  @Test
  void agregarSugerencia_CuandoSugerenciaValida_DebeAgregarALaLista() {
    solicitud.agregarSugerencia("Sugerencia válida");

    assertFalse(solicitud.getSugerencias().isEmpty());
  }

  @Test
  void agregarSugerencia_CuandoSugerenciaNula_DebeLanzarExcepcion() {
    assertThrows(
        IllegalArgumentException.class,
        () -> solicitud.agregarSugerencia(null)
    );
  }
}
