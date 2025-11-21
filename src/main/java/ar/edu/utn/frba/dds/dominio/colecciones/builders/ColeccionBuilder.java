package ar.edu.utn.frba.dds.dominio.colecciones.builders;

import ar.edu.utn.frba.dds.dominio.colecciones.Coleccion;
import ar.edu.utn.frba.dds.dominio.colecciones.ModoDeNavegacion;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroHecho;
import ar.edu.utn.frba.dds.dominio.fuentes.Fuente;
import java.util.ArrayList;
import java.util.List;

public class ColeccionBuilder {
  private String titulo;
  private String descripcion;
  private final List<FiltroHecho> filtros = new ArrayList<>();
  private ModoDeNavegacion modoDeNavegacion = ModoDeNavegacion.IRRESTRICTO;
  private Fuente fuente;

  public ColeccionBuilder conTitulo(String titulo) {
    this.titulo = titulo;
    return this;
  }

  public ColeccionBuilder conDescripcion(String descripcion) {
    this.descripcion = descripcion;
    return this;
  }

  public ColeccionBuilder conFiltro(FiltroHecho filtroHecho) {
    this.filtros.add(filtroHecho);
    return this;
  }

  public ColeccionBuilder conFiltros(List<FiltroHecho> filtros) {
    this.filtros.addAll(filtros);
    return this;
  }

  public ColeccionBuilder conModoDeNavegacion(ModoDeNavegacion modoDeNavegacion) {
    this.modoDeNavegacion = modoDeNavegacion;
    return this;
  }

  public ColeccionBuilder conFuente(Fuente fuente) {
    this.fuente = fuente;
    return this;
  }

  public Coleccion build() {
    if (fuente == null) {
      throw new IllegalStateException("La fuente es obligatoria");
    }

    return new Coleccion(
        titulo,
        descripcion,
        fuente,
        new CriterioDePertenencia(this.filtros),
        this.modoDeNavegacion);
  }
}
