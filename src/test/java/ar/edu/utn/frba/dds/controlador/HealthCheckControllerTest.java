package ar.edu.utn.frba.dds.controlador;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HealthCheckControllerTest {

  @Test
  public void testStatus() {
    HealthCheckController healthCheckController = new HealthCheckController();
    assertEquals("ok", healthCheckController.healtcheck().get("status"));
  }

}