package ar.edu.utn.frba.dds.dominio.estadisticas;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "estadistica_resultado")
public class EstadisticaResultado {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public String nombre;

  public String resultado;

  public LocalDateTime fechaCreacion;

  public LocalDateTime fechaBusquedaInicio;

  public LocalDateTime fechaBusquedaFin;

  public EstadisticaResultado(
          String nombre, String resultado, String parametro,
          LocalDateTime fechaBusquedaInicio, LocalDateTime fechaBusquedaFin) {
    this.nombre =
        nombre + (parametro != null && !parametro.trim().isEmpty() ? ":" + parametro : "");
    this.resultado = resultado;
    this.fechaCreacion = LocalDateTime.now();
    this.fechaBusquedaInicio = fechaBusquedaInicio;
    this.fechaBusquedaFin = fechaBusquedaFin;
  }

  public EstadisticaResultado() {}

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public String getResultado() {
    return resultado;
  }

  public LocalDateTime getFechaBusquedaFin() {
    return fechaBusquedaFin;
  }

  public LocalDateTime getFechaBusquedaInicio() {
    return fechaBusquedaInicio;
  }
}
