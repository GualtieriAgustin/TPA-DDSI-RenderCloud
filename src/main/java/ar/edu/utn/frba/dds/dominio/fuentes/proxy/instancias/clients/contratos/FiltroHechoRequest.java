package ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import java.time.LocalDateTime;

public class FiltroHechoRequest {
  private String categoria;
  private LocalDateTime fechaReporteDesde;
  private LocalDateTime fechaReporteHasta;
  private LocalDateTime fechaAcontecimientoDesde;
  private LocalDateTime fechaAcontecimientoHasta;
  private Ubicacion ubicacion;

  public FiltroHechoRequest() {
    this.categoria = "";
    this.fechaReporteDesde = null;
    this.fechaReporteHasta = null;
    this.fechaAcontecimientoDesde = null;
    this.fechaAcontecimientoHasta = null;
    this.ubicacion = null;
  }

  private FiltroHechoRequest(
      String categoria,
      LocalDateTime fechaReporteDesde,
      LocalDateTime fechaReporteHasta,
      LocalDateTime fechaAcontecimientoDesde,
      LocalDateTime fechaAcontecimientoHasta,
      Ubicacion ubicacion) {
    this.categoria = categoria;
    this.fechaReporteDesde = fechaReporteDesde;
    this.fechaReporteHasta = fechaReporteHasta;
    this.fechaAcontecimientoDesde = fechaAcontecimientoDesde;
    this.fechaAcontecimientoHasta = fechaAcontecimientoHasta;
    this.ubicacion = ubicacion;
  }

  public FiltroHechoRequest conCategoria(String categoria) {
    this.categoria = categoria;
    return this;
  }

  public FiltroHechoRequest conFechaReporteDesde(LocalDateTime fechaReporteDesde) {
    this.fechaReporteDesde = fechaReporteDesde;
    return this;
  }

  public FiltroHechoRequest conFechaReporteHasta(LocalDateTime fechaReporteHasta) {
    this.fechaReporteHasta = fechaReporteHasta;
    return this;
  }

  public FiltroHechoRequest conFechaAcontecimientoDesde(LocalDateTime fechaAcontecimientoDesde) {
    this.fechaAcontecimientoDesde = fechaAcontecimientoDesde;
    return this;
  }

  public FiltroHechoRequest conFechaAcontecimientoHasta(LocalDateTime fechaAcontecimientoHasta) {
    this.fechaAcontecimientoHasta = fechaAcontecimientoHasta;
    return this;
  }

  public FiltroHechoRequest conUbicacion(Ubicacion ubicacion) {
    this.ubicacion = ubicacion;
    return this;
  }

  public FiltroHechoRequest create() {
    return new FiltroHechoRequest(
        this.categoria,
        this.fechaReporteDesde,
        this.fechaReporteHasta,
        this.fechaAcontecimientoDesde,
        this.fechaAcontecimientoHasta,
        this.ubicacion);
  }

  public String getCategoria() {
    return categoria != null && !categoria.isEmpty() ? categoria : null;
  }

  public String getFechaReporteDesde() {
    return fechaReporteDesde != null ? fechaReporteDesde.toString() : null;
  }

  public String getFechaReporteHasta() {
    return fechaReporteHasta != null ? fechaReporteHasta.toString() : null;
  }

  public String getFechaAcontecimientoDesde() {
    return fechaAcontecimientoDesde != null ? fechaAcontecimientoDesde.toString() : null;
  }

  public String getFechaAcontecimientoHasta() {
    return fechaAcontecimientoHasta != null ? fechaAcontecimientoHasta.toString() : null;
  }

  public String getUbicacion() {
    if (ubicacion == null) {
      return null;
    }
    Double latitud = ubicacion.getLatitud();
    Double longitud = ubicacion.getLongitud();
    return (latitud != null && longitud != null) ? latitud + "," + longitud : null;
  }

  public static FiltroHechoRequest crearFrom(CriterioDePertenencia criterio) {
    FiltroHechoRequest filtroHechoRequest = new FiltroHechoRequest();
    criterio.aplicarA(filtroHechoRequest);
    return filtroHechoRequest;
  }

}