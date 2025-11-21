package ar.edu.utn.frba.dds.dominio.fuentes.cache;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.fuentes.Fuente;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.util.List;
import java.util.Optional;

public abstract class FuenteCacheable extends Fuente {

  private final CacheDeHechos cacheDeHechos;

  public FuenteCacheable() {
    this.cacheDeHechos = new CacheDeHechos();
  }

  // TODO: Cambiar nombre para no confundir con obtenerHechosPorCriterio
  public List<Hecho> getHechosPorCriterio(CriterioDePertenencia criterio) {
    Optional<List<Hecho>> hechosEnCache = cacheDeHechos.getHechos(criterio);
    if (hechosEnCache.isEmpty()) {
      List<Hecho> hechos = obtenerHechosPorCriterio(criterio);
      cacheDeHechos.actualizarCache(criterio, hechos);
      return hechos;
    } else {
      return hechosEnCache.get();
    }
  }

  public void refrescarCache() {
    cacheDeHechos.criteriosEnCache().forEach(criterio
        -> cacheDeHechos.actualizarCache(criterio, obtenerHechosPorCriterio(criterio)));
  }
}
