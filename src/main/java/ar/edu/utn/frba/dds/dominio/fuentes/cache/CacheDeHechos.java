package ar.edu.utn.frba.dds.dominio.fuentes.cache;


import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CacheDeHechos implements Cache {

  Map<CriterioDePertenencia, HechosConTiempo> hechosEnMemoria;


  //Inicializacion

  public CacheDeHechos() {
    hechosEnMemoria = new HashMap<>();
  }

  public Set<CriterioDePertenencia> criteriosEnCache() {
    return hechosEnMemoria.keySet();
  }

  //Consultar a la memoria

  @Override
  public Optional<List<Hecho>> getHechos(CriterioDePertenencia filtroHecho) {
    if (hechosEnMemoria.containsKey(filtroHecho)) {
      if (estaVencido(hechosEnMemoria.get(filtroHecho))) {
        this.eliminarVencidos();
        return Optional.empty();
      } else {
        return Optional.of(hechosEnMemoria.get(filtroHecho).getHechos());
      }
    } else {
      return Optional.empty();
    }
  }

  //Actualizacion de memoria con nuevos valores desde la fuente

  @Override
  public void actualizarCache(CriterioDePertenencia filtroHecho, List<Hecho> hechos) {
    HechosConTiempo memoria = new HechosConTiempo(hechos);
    hechosEnMemoria.put(filtroHecho, memoria);
  }

  //Logica de lifecycle

  @Override
  public void eliminarVencidos() {
    hechosEnMemoria.entrySet().removeIf(entry -> estaVencido(entry.getValue()));
  }

  private Boolean estaVencido(HechosConTiempo memoria) {
    return memoria.getTimestamp().isBefore(LocalDateTime.now().minusHours(1));
  }
}

