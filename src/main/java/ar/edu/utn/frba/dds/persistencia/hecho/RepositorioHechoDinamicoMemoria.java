package ar.edu.utn.frba.dds.persistencia.hecho;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.exceptions.HechoNoCreadoPorUsuarioException;
import java.util.ArrayList;
import java.util.List;

public class RepositorioHechoDinamicoMemoria implements RepositorioHechoDinamico {

  private final List<Hecho> hechos;
  private Long proximoId = 1L;

  public RepositorioHechoDinamicoMemoria() {
    this.hechos = new ArrayList<>();
  }

  @Override
  public Hecho crear(Hecho hecho) {
    if (!hecho.esCredoPorUsuario()) {
      throw new HechoNoCreadoPorUsuarioException();
    }
    // Luego realmente aca se le asignaria un ID unico al hecho.
    hecho.setId(proximoId);
    proximoId++;
    this.hechos.add(hecho);
    return hecho;
  }

  @Override
  public List<Hecho> consultarTodos() {
    return List.copyOf(this.hechos);
  }

  @Override
  public List<Hecho> consultarPorCriterio(CriterioDePertenencia criterio) {
    return this.hechos.stream()
        .filter(h -> criterio.cumple(h))
        .toList();
  }

  @Override
  public Hecho consultarPorId(Long id) {
    return this.hechos.stream()
        .filter(h -> h.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<Hecho> consultarPorUsuario(Long userId) {
    return this.hechos.stream()
        .filter(h -> h.getUsuarioId().equals(userId))
        .toList();
  }

  @Override
  public void actualizar(Hecho hecho) {
    var existeHecho = consultarPorId(hecho.getId());

    if (existeHecho != null) {
      this.hechos.remove(existeHecho);
      this.hechos.add(hecho);
    }
  }

}
