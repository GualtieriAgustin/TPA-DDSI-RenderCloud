package ar.edu.utn.frba.dds.dominio.solicitudes.modificacion;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ModificacionHecho {

  @Column(name = "modificacion_titulo")
  private String titulo;
  @Column(name = "modificacion_descripcion")
  private String descripcion;
  @Column(name = "modificacion_categoria")
  private String categoria;
  private Ubicacion ubicacion;

  protected ModificacionHecho() {
  }

  public ModificacionHecho(
      String titulo,
      String descripcion,
      String categoria,
      Ubicacion ubicacion
  ) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.ubicacion = ubicacion;
  }

  public void aplicarA(Hecho hecho) {
    if (titulo != null) {
      hecho.setTitulo(titulo);
    }
    if (descripcion != null) {
      hecho.setDescripcion(descripcion);
    }
    if (categoria != null) {
      hecho.setCategoria(categoria);
    }
    if (ubicacion != null) {
      hecho.setUbicacion(ubicacion);
    }
  }

  public String getTitulo() {
    return titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public String getCategoria() {
    return categoria;
  }

  public Ubicacion getUbicacion() {
    return ubicacion;
  }
}
