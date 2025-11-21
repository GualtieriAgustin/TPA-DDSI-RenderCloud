package ar.edu.utn.frba.dds.dominio.colecciones.filtros.fecha.exacta;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.time.LocalDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Filtro por fecha de carga.
 */
@Entity
@DiscriminatorValue("FECHA_CARGA")
public class FiltroFechaCarga extends FiltroFecha {

  public FiltroFechaCarga(LocalDateTime fechaCarga) {
    super(fechaCarga);
  }

  protected FiltroFechaCarga() {
    super();
  }

  @Override
  LocalDateTime fechaComparacion(Hecho hecho) {
    return hecho.getFechaCarga();
  }
}
