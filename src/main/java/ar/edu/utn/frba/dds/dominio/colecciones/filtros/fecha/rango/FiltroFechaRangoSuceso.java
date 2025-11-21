package ar.edu.utn.frba.dds.dominio.colecciones.filtros.fecha.rango;

import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos.FiltroHechoRequest;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.time.LocalDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("FECHA_RANGO_SUCESO")
public class FiltroFechaRangoSuceso extends FiltroFechaRango {

  private static final String fechaSuceso = "fechaSuceso";

  public FiltroFechaRangoSuceso(LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
    super(fechaDesde, fechaHasta, fechaSuceso);
  }

  protected FiltroFechaRangoSuceso() {
    super();
  }

  @Override
  LocalDateTime fechaComparacion(Hecho hecho) {
    return hecho.getFechaSuceso();
  }

  @Override
  public void aplicarA(FiltroHechoRequest filtroHechoRequest) {
    filtroHechoRequest.conFechaAcontecimientoDesde(fechaDesde);
    filtroHechoRequest.conFechaAcontecimientoHasta(fechaHasta);
  }
}
