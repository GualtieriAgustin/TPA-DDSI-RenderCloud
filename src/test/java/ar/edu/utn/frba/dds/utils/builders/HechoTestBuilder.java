package ar.edu.utn.frba.dds.utils.builders;

import ar.edu.utn.frba.dds.dominio.fuentes.Origen;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.Multimedia;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HechoTestBuilder {
  private String titulo;
  private String descripcion;
  private String categoria;
  private Ubicacion ubicacion;
  private LocalDateTime fechaSuceso;
  private Origen origen;
  private LocalDateTime fechaCarga;
  private final List<Multimedia> multimedia;
  private Usuario usuario;
  private Provincia provincia;

  public HechoTestBuilder() {
    this.titulo = "Título por defecto";
    this.descripcion = "Descripción por defecto";
    this.categoria = "Categoría por defecto";
    this.ubicacion = new Ubicacion(10.0, 20.0);
    this.fechaSuceso = LocalDateTime.now().minusDays(3);
    this.origen = Origen.DATASET;
    this.fechaCarga = LocalDateTime.now();
    this.multimedia = new ArrayList<>();
    this.provincia = Provincia.PROVINCIA_DESCONOCIDA;
  }

  public HechoTestBuilder conTitulo(String titulo) {
    this.titulo = titulo;
    return this;
  }

  public HechoTestBuilder conDescripcion(String descripcion) {
    this.descripcion = descripcion;
    return this;
  }

  public HechoTestBuilder conProvincia(Provincia provincia) {
    this.provincia = provincia;
    return this;
  }

  public HechoTestBuilder conCategoria(String categoria) {
    this.categoria = categoria;
    return this;
  }

  public HechoTestBuilder conUbicacion(Ubicacion ubicacion) {
    this.ubicacion = ubicacion;
    return this;
  }

  public HechoTestBuilder conFechaSuceso(LocalDateTime fechaSuceso) {
    this.fechaSuceso = fechaSuceso;
    return this;
  }

  public HechoTestBuilder conOrigen(Origen origen) {
    this.origen = origen;
    return this;
  }

  public HechoTestBuilder conFechaCarga(LocalDateTime fechaCarga) {
    this.fechaCarga = fechaCarga;
    return this;
  }

  public HechoTestBuilder conMultimedia(Multimedia multimedia) {
    this.multimedia.add(multimedia);
    return this;
  }

  public HechoTestBuilder conUsuario(Usuario usuario) {
    this.usuario = usuario;
    return this;
  }

  public Hecho build() {
    if(this.usuario == null){
      return new Hecho(
          titulo,
          descripcion,
          categoria,
          ubicacion,
          fechaSuceso,
          origen,
          fechaCarga,
          multimedia,
          provincia);
    }

    return new Hecho(
        titulo,
        descripcion,
        categoria,
        ubicacion,
        fechaSuceso,
        fechaCarga,
        multimedia,
        usuario,
        provincia);
    }
}
