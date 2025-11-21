package ar.edu.utn.frba.dds.dominio.fuentes.cache;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HechosConTiempo {

  private final List<Hecho> hechos;
  private final LocalDateTime timestamp;

  public HechosConTiempo(List<Hecho> hechos) {
    this.hechos = new ArrayList<>(hechos);
    this.timestamp = LocalDateTime.now();
  }

  public List<Hecho> getHechos() {
    return Collections.unmodifiableList(hechos);
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }
}
