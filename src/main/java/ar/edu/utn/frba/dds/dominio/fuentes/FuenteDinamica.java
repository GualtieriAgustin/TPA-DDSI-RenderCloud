package ar.edu.utn.frba.dds.dominio.fuentes;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.fuentes.cache.FuenteCacheable;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioDeHechos;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioHechoDinamico;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("DINAMICA")
public class FuenteDinamica extends FuenteCacheable {

  @Transient
  private RepositorioHechoDinamico repositorioHechos;

  public FuenteDinamica(RepositorioHechoDinamico repositorioHechos) {
    super();
    this.repositorioHechos = repositorioHechos;
  }

  public FuenteDinamica() {
    // JPA requiere un constructor vacío
    this.repositorioHechos = null;
  }

  public void setRepositorioHechos(RepositorioHechoDinamico repositorioHechos) {
    this.repositorioHechos = repositorioHechos;
  }

  @Override
  public List<Hecho> getHechos() {
    return repositorioHechos.consultarTodos();
  }

  // Evaluar si en un futuro armamos un filtro por usuario dentro del criterio
  public List<Hecho> getHechosPorUsuario(Long userId) {
    return repositorioHechos.consultarPorUsuario(userId);
  }

  @Override
  public List<Hecho> obtenerHechosPorCriterio(CriterioDePertenencia criterio) {
    return repositorioHechos.consultarPorCriterio(criterio);
  }

  @PostLoad
  public void inicializarFuente() {
    if (this.repositorioHechos == null) {
      this.repositorioHechos = new RepositorioDeHechos();
    }
  }
}
