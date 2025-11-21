package ar.edu.utn.frba.dds.dominio.solicitudes.observers;

import ar.edu.utn.frba.dds.dominio.solicitudes.baja.SolicitudBajaHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.procesador.ProcesadorDeSolicitudesDeBaja;

public interface SolicitudBajaObserver {
  void solicitudBajaCreada(
      SolicitudBajaHecho solicitudBajaHecho,
      ProcesadorDeSolicitudesDeBaja procesadorDeSolicitudesDeBaja
  );
}
