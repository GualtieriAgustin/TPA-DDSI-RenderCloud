package ar.edu.utn.frba.dds.dominio.solicitudes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.solicitudes.baja.SolicitudBajaHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.exceptions.TransicionEstadoSolicitudInvalidaException;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SolicitudBajaHechoTest {

  CriterioDePertenencia criterio;

  @BeforeEach
  void setUp() {
    criterio = new CriterioDePertenencia();
  }

  @Test
  void aceptar_CuandoSeInvoca_DebeCambiarEstadoAAceptada() {
    SolicitudBajaHecho solicitud = crearSolicitudValida();
    solicitud.aceptar();
    assertEquals(EstadoSolicitud.ACEPTADA, solicitud.getEstado());
  }

  @Test
  void rechazar_CuandoSeInvoca_DebeCambiarEstadoARechazada() {
    SolicitudBajaHecho solicitud = crearSolicitudValida();
    solicitud.rechazar();
    assertEquals(EstadoSolicitud.RECHAZADA, solicitud.getEstado());
  }

  @Test
  void aceptar_CuandoSeInvocaMultiplesVeces_DebeMantenerEstadoAceptada() {
    SolicitudBajaHecho solicitud = crearSolicitudValida();
    solicitud.aceptar();
    solicitud.aceptar();
    solicitud.aceptar();
    assertEquals(EstadoSolicitud.ACEPTADA, solicitud.getEstado());
  }

  @Test
  void rechazar_CuandoSeInvocaMultiplesVeces_DebeMantenerEstadoRechazada() {
    SolicitudBajaHecho solicitud = crearSolicitudValida();
    solicitud.rechazar();
    solicitud.rechazar();
    solicitud.rechazar();
    assertEquals(EstadoSolicitud.RECHAZADA, solicitud.getEstado());
  }

  @Test
  void rechazar_CuandoYaEstaAceptada_DebeLanzarExcepcion() {
    SolicitudBajaHecho solicitud = crearSolicitudValida();
    solicitud.aceptar();

    TransicionEstadoSolicitudInvalidaException exception = assertThrows(
        TransicionEstadoSolicitudInvalidaException.class,
        solicitud::rechazar
    );
    assertEquals("No se puede pasar del estado ACEPTADA al estado RECHAZADA", exception.getMessage());
  }

  @Test
  void aceptar_CuandoYaEstaRechazada_DebeLanzarExcepcion() {
    SolicitudBajaHecho solicitud = crearSolicitudValida();
    solicitud.rechazar();

    TransicionEstadoSolicitudInvalidaException exception = assertThrows(
        TransicionEstadoSolicitudInvalidaException.class,
        solicitud::aceptar
    );
    assertEquals("No se puede pasar del estado RECHAZADA al estado ACEPTADA", exception.getMessage());
  }

  SolicitudBajaHecho crearSolicitudValida() {
    Usuario usuario = new Usuario("test");
    return new SolicitudBajaHecho(criterio, "a".repeat(500), usuario);
  }
}