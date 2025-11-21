package ar.edu.utn.frba.dds.dominio.solicitudes.baja;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.solicitudes.SolicitudHecho;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@DiscriminatorValue("BAJA")
public class SolicitudBajaHecho extends SolicitudHecho {

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "criterio_de_baja_id")
  CriterioDePertenencia criterioDeBaja;

  public SolicitudBajaHecho(
      CriterioDePertenencia criterioDeBaja,
      String descripcion,
      Usuario solicitante
  ) {
    super(descripcion, solicitante);
    this.criterioDeBaja = criterioDeBaja;
  }

  protected SolicitudBajaHecho() {

  }

  public CriterioDePertenencia getCriterioDeBaja() {
    return criterioDeBaja;
  }
}
