package ar.edu.utn.frba.dds.dominio.solicitudes.modificacion;

import ar.edu.utn.frba.dds.dominio.solicitudes.SolicitudHecho;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("MODIFICACION")
public class SolicitudModificacionHecho extends SolicitudHecho {

  @Embedded
  ModificacionHecho modificacionHecho;
  @Column(name = "hecho_id")
  private Long hechoId;
  @ElementCollection
  @CollectionTable(name = "solicitud_modificacion_sugerencias")
  private List<String> sugerencias;

  public SolicitudModificacionHecho(
      Long hechoId,
      String justificacion,
      Usuario solicitante,
      ModificacionHecho modificacionHecho) {
    super(justificacion, solicitante);
    this.hechoId = hechoId;
    this.sugerencias = new ArrayList<>();
    this.modificacionHecho = modificacionHecho;
  }

  protected SolicitudModificacionHecho() {

  }

  public Long getId() {
    return this.id;
  }

  public Long getUserId() {
    return this.solicitante.getId();
  }

  public void agregarSugerencia(String sugerencia) {
    if (sugerencia == null || sugerencia.isBlank()) {
      throw new IllegalArgumentException("La sugerencia no puede ser nula o vacía");
    }
    this.sugerencias.add(sugerencia);
  }

  public List<String> getSugerencias() {
    return List.copyOf(this.sugerencias);
  }

  public ModificacionHecho getModificacionHecho() {
    return modificacionHecho;
  }

  public Long getHechoId() {
    return hechoId;
  }
}
