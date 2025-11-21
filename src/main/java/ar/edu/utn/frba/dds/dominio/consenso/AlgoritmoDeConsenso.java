package ar.edu.utn.frba.dds.dominio.consenso;

import ar.edu.utn.frba.dds.dominio.consenso.estrategias.AbsolutaEstrategia;
import ar.edu.utn.frba.dds.dominio.consenso.estrategias.MayoriaSimpleEstrategia;
import ar.edu.utn.frba.dds.dominio.consenso.estrategias.MultiplesMencionesEstrategia;

/**
 * Enum que actúa como un catálogo y fábrica de estrategias de consenso.
 * Es ideal para persistir en BD (guardando el nombre) y para poblar UIs.
 */
public enum AlgoritmoDeConsenso {

  MULTIPLES_MENCIONES(new MultiplesMencionesEstrategia()),
  MAYORIA_SIMPLE(new MayoriaSimpleEstrategia()),
  ABSOLUTA(new AbsolutaEstrategia());

  private final EstrategiaDeConsenso estrategia;

  AlgoritmoDeConsenso(EstrategiaDeConsenso estrategia) {
    this.estrategia = estrategia;
  }

  public EstrategiaDeConsenso getEstrategia() {
    return this.estrategia;
  }
}