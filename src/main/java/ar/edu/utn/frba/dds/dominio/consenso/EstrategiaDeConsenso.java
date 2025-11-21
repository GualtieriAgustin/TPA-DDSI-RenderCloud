package ar.edu.utn.frba.dds.dominio.consenso;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;

public interface EstrategiaDeConsenso {
  NivelDeConsenso calcular(Hecho hecho, DatosGlobalesDeConsenso datos);
}
