package ar.edu.utn.frba.dds.dominio.hechos;

import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class Ubicacion {

  private Double latitud;
  private Double longitud;

  protected Ubicacion() {
  }

  public Ubicacion(Double latitud, Double longitud) {
    this.latitud = latitud;
    this.longitud = longitud;
  }

  public Double getLatitud() {
    return latitud;
  }

  public Double getLongitud() {
    return longitud;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Ubicacion ubicacion = (Ubicacion) o;
    return Objects.equals(getLatitud(), ubicacion.getLatitud())
        && Objects.equals(getLongitud(), ubicacion.getLongitud());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getLatitud(), getLongitud());
  }

  @Override
  public String toString() {
    return "Ubicacion{"
        + "latitud=" + latitud
        + ", longitud=" + longitud
        + '}';
  }
}
