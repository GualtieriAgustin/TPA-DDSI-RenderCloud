package ar.edu.utn.frba.dds.dominio.colecciones.filtros.fecha.exacta;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.time.LocalDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Filtro por fecha del suceso.
 */
@Entity
@DiscriminatorValue("FECHA_SUCESO")
public class FiltroFechaSuceso extends FiltroFecha {

  public FiltroFechaSuceso(LocalDateTime fechaSuceso) {
    super(fechaSuceso);
  }

  protected FiltroFechaSuceso() {
    super();
  }

  @Override
  LocalDateTime fechaComparacion(Hecho hecho) {
    return hecho.getFechaSuceso();
  }
}
