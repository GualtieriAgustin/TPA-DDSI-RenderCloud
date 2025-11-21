package ar.edu.utn.frba.dds.dominio.fuentes.cache;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.util.List;
import java.util.Optional;

public interface Cache {

  Optional<List<Hecho>> getHechos(CriterioDePertenencia filtroHecho);

  void actualizarCache(CriterioDePertenencia filtroHecho, List<Hecho> hechos);

  void eliminarVencidos();
}

