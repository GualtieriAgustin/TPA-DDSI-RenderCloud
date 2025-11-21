package ar.edu.utn.frba.dds.dominio.usuarios;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.EstadoSolicitud;
import ar.edu.utn.frba.dds.dominio.solicitudes.baja.SolicitudBajaHecho;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContribuyenteTest {


  @BeforeEach
  void setUp() {

  }

  @Test
  public void crearSolicitudDeEliminacion_CuandoSeProporcionaHechoYDescripcion_DeberiaRetornarSolicitudPendiente() {
    //Arrange
    Hecho hecho = new HechoTestBuilder().build();
    String descripcion = "a".repeat(500);
    CriterioDePertenencia criterioDeBaja = new CriterioDePertenencia();
    Usuario usuario = new Usuario("test");

    //Act
    SolicitudBajaHecho solicitud = new SolicitudBajaHecho(criterioDeBaja, descripcion, usuario);

    //Assert
    assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
    assertEquals(descripcion, solicitud.getDescripcion());
    assertEquals(criterioDeBaja, solicitud.getCriterioDeBaja());
  }
}
