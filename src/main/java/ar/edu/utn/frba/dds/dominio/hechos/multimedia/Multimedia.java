package ar.edu.utn.frba.dds.dominio.hechos.multimedia;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class Multimedia {
  private String nombre;
  private TipoMultimedia tipo;
  private String descripcion;

  @Transient
  private String url;

  protected Multimedia() {}

  public Multimedia(String nombre, TipoMultimedia tipo, String descripcion) {
    this.nombre = nombre;
    this.tipo = tipo;
    this.descripcion = descripcion;
  }

  public String getNombre() {
    return nombre;
  }

  public String getUrl() {
    return url;
  }

  public TipoMultimedia getTipo() {
    return tipo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}