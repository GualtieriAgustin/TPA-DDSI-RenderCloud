package ar.edu.utn.frba.dds.dominio.hechos;

import ar.edu.utn.frba.dds.dominio.fuentes.Origen;
import ar.edu.utn.frba.dds.dominio.hechos.exceptions.HechoNoCreadoPorUsuarioException;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.Multimedia;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Hecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String titulo;

  @Column(length = 1000)
  private String descripcion;

  private String categoria;

  @Embedded
  private Ubicacion ubicacion;

  @Column(name = "fecha_suceso")
  private LocalDateTime fechaSuceso;

  @Enumerated(EnumType.STRING)
  private Origen origen;

  @Column(name = "fecha_carga")
  private LocalDateTime fechaCarga;

  @Enumerated(EnumType.STRING)
  private Provincia provincia = Provincia.PROVINCIA_DESCONOCIDA;

  @ElementCollection
  private List<Multimedia> multimedias;

  @ManyToOne
  private Usuario usuario;

  protected Hecho() {}

  public Hecho(
      String titulo,
      String descripcion,
      String categoria,
      Ubicacion ubicacion,
      LocalDateTime fechaSuceso,
      Origen origen,
      LocalDateTime fechaCarga,
      Provincia provincia) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.ubicacion = ubicacion;
    this.fechaSuceso = fechaSuceso;
    this.origen = origen;
    this.fechaCarga = fechaCarga;
    this.multimedias = List.of();
    this.provincia = provincia;
  }

  public Hecho(
      String titulo,
      String descripcion,
      String categoria,
      Ubicacion ubicacion,
      LocalDateTime fechaSuceso,
      Origen origen,
      LocalDateTime fechaCarga,
      List<Multimedia> multimedias,
      Provincia provincia) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.ubicacion = ubicacion;
    this.fechaSuceso = fechaSuceso;
    this.origen = origen;
    this.fechaCarga = fechaCarga;
    this.multimedias = (multimedias == null) ? List.of() : List.copyOf(multimedias);
    this.provincia = provincia;
  }

  public Hecho(
      String titulo,
      String descripcion,
      String categoria,
      Ubicacion ubicacion,
      LocalDateTime fechaSuceso,
      LocalDateTime fechaCarga,
      List<Multimedia> multimedia,
      Usuario usuario,
      Provincia provincia) {
    this(titulo,
        descripcion,
        categoria,
        ubicacion,
        fechaSuceso,
        Origen.CONTRIBUYENTE,
        fechaCarga,
        multimedia,
        provincia);
    if (usuario == null) {
      throw new HechoNoCreadoPorUsuarioException();
    }
    this.usuario = usuario;
  }

  public Long getId() {
    return id;
  }

  public Long getUsuarioId() {
    return usuario != null ? usuario.getId() : null;
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

  public Double getLatitud() {
    return ubicacion.getLatitud();
  }

  public Double getLongitud() {
    return ubicacion.getLongitud();
  }

  public LocalDateTime getFechaSuceso() {
    return fechaSuceso;
  }

  public Origen getOrigen() {
    return origen;
  }

  public LocalDateTime getFechaCarga() {
    return fechaCarga;
  }

  public List<Multimedia> getMultimedias() {
    return List.copyOf(multimedias);
  }

  public Provincia getProvincia() {
    return provincia;
  }

  public String getNombreProvincia() {
    return provincia != null ? provincia.getNombreGeoJson() : "No especificada";
  }

  public boolean esCredoPorUsuario() {
    return this.usuario != null && origen == Origen.CONTRIBUYENTE;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Hecho hecho = (Hecho) o;
    return Objects.equals(getTitulo(), hecho.getTitulo())
        && Objects.equals(getDescripcion(), hecho.getDescripcion())
        && Objects.equals(getCategoria(), hecho.getCategoria())
        && Objects.equals(getUbicacion(), hecho.getUbicacion())
        && Objects.equals(getFechaSuceso(), hecho.getFechaSuceso())
        && getOrigen() == hecho.getOrigen()
        && Objects.equals(getFechaCarga(), hecho.getFechaCarga())
        && Objects.equals(multimedias, hecho.multimedias);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getTitulo(),
        getDescripcion(),
        getCategoria(),
        getUbicacion(),
        getFechaSuceso(),
        getOrigen(),
        getFechaCarga(),
        getMultimedias());
  }

  @Override
  public String toString() {
    return "Hecho{"
        + "titulo='" + titulo + '\''
        + ", descripcion='" + descripcion + '\''
        + ", categoria='" + categoria + '\''
        + ", ubicacion=" + ubicacion
        + ", fechaSuceso=" + fechaSuceso
        + ", origen=" + origen
        + ", fechaCarga=" + fechaCarga
        + ", multimedia=" + multimedias
        + '}';
  }

  public void setTitulo(String titulo) {
    if (titulo == null || titulo.isBlank()) {
      throw new IllegalArgumentException("El titulo no puede ser nulo o estar en blanco");
    }
    this.titulo = titulo;
  }

  public void setDescripcion(String descripcion) {
    if (descripcion == null || descripcion.isBlank()) {
      throw new IllegalArgumentException("La descripcion no puede ser nula o estar en blanco");
    }
    this.descripcion = descripcion;
  }

  public void setCategoria(String categoria) {
    if (categoria == null || categoria.isBlank()) {
      throw new IllegalArgumentException("La categoria no puede ser nula o estar en blanco");
    }
    this.categoria = categoria;
  }

  public void setUbicacion(Ubicacion ubicacion) {
    if (ubicacion == null) {
      throw new IllegalArgumentException("La ubicacion no puede ser nula");
    }
    this.ubicacion = ubicacion;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public void setOrigen(Origen origen) {
    this.origen = origen;
  }
}
