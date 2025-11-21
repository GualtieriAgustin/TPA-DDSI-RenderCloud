package ar.edu.utn.frba.dds.controlador;

import java.util.Map;

public class HealthCheckController {
  public Map<String, String> healtcheck() {
    return Map.of("status", "ok");
  }
}
