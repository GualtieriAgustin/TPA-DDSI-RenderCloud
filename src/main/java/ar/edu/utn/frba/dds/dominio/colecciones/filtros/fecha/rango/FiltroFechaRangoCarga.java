package ar.edu.utn.frba.dds.dominio.colecciones.filtros.fecha.rango;

import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos.FiltroHechoRequest;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.time.LocalDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Entity
@DiscriminatorValue("FECHA_RANGO_CARGA")
public class FiltroFechaRangoCarga extends FiltroFechaRango {

  private static final String fechaCarga = "fechaCarga";

  public FiltroFechaRangoCarga(LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
    super(fechaDesde, fechaHasta, fechaCarga);
  }

  protected FiltroFechaRangoCarga() {
    super();
  }

  @Override
  LocalDateTime fechaComparacion(Hecho hecho) {
    return hecho.getFechaCarga();
  }

  @Override
  public void aplicarA(FiltroHechoRequest filtroHechoRequest) {
    filtroHechoRequest.conFechaReporteDesde(fechaDesde);
    filtroHechoRequest.conFechaReporteHasta(fechaHasta);
  }

  @Override
  public Predicate predicado(CriteriaBuilder cb, Root<Hecho> root) {
    return cb.between(root.get(fechaCarga), fechaDesde, fechaHasta);
  }
}
