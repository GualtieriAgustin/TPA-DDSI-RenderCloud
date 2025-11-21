package ar.edu.utn.frba.dds.persistencia.hecho;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.util.List;

public interface RepositorioHechoDinamico {

  Hecho crear(Hecho hecho);

  List<Hecho> consultarTodos();

  List<Hecho> consultarPorCriterio(CriterioDePertenencia criterio);

  Hecho consultarPorId(Long id);

  List<Hecho> consultarPorUsuario(Long idUsuario);

  void actualizar(Hecho hecho);
}
