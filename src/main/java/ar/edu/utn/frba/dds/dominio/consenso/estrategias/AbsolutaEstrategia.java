package ar.edu.utn.frba.dds.dominio.consenso.estrategias;

import ar.edu.utn.frba.dds.dominio.consenso.DatosGlobalesDeConsenso;
import ar.edu.utn.frba.dds.dominio.consenso.EstrategiaDeConsenso;
import ar.edu.utn.frba.dds.dominio.consenso.NivelDeConsenso;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;

public class AbsolutaEstrategia implements EstrategiaDeConsenso {
  @Override
  public NivelDeConsenso calcular(Hecho hecho, DatosGlobalesDeConsenso datos) {
    boolean consensuado = datos.mencionesDelHecho() == datos.totalDeFuentes();
    return consensuado ? NivelDeConsenso.CONSENSUADO : NivelDeConsenso.NO_CONSENSUADO;
  }
}
