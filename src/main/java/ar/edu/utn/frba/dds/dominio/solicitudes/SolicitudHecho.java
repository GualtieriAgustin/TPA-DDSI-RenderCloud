package ar.edu.utn.frba.dds.dominio.solicitudes;

import ar.edu.utn.frba.dds.dominio.solicitudes.exceptions.TransicionEstadoSolicitudInvalidaException;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "solicitud_hecho")
@DiscriminatorColumn(name = "tipo_solicitud", discriminatorType = DiscriminatorType.STRING)
public abstract class SolicitudHecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @Column(columnDefinition = "TEXT", name = "solicitud_descripcion")
  protected String descripcion;

  @Enumerated(EnumType.STRING)
  protected EstadoSolicitud estado;

  @Column(name = "fecha_creacion")
  protected LocalDateTime fechaCreacion;

  @ManyToOne
  protected Usuario solicitante;

  protected SolicitudHecho(String descripcion, Usuario solicitante) {
    this.descripcion = descripcion;
    this.estado = EstadoSolicitud.PENDIENTE;
    this.fechaCreacion = LocalDateTime.now();
    this.solicitante = solicitante;
  }

  protected SolicitudHecho() {

  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public EstadoSolicitud getEstado() {
    return estado;
  }


  public void aceptar() {
    cambiarEstado(EstadoSolicitud.ACEPTADA);
  }

  public void rechazar() {
    cambiarEstado(EstadoSolicitud.RECHAZADA);
  }

  protected boolean puedeCambiarA(EstadoSolicitud nuevoEstado) {
    return estado == EstadoSolicitud.PENDIENTE && estado != nuevoEstado;
  }

  protected void cambiarEstado(EstadoSolicitud nuevoEstado) {
    if (estado == nuevoEstado) {
      return;
    }
    if (!puedeCambiarA(nuevoEstado)) {
      throw new TransicionEstadoSolicitudInvalidaException(estado, nuevoEstado);
    }
    estado = nuevoEstado;
  }
}
