package ar.edu.utn.frba.dds.dominio.solicitudes.observers.spam;

import ar.edu.utn.frba.dds.dominio.solicitudes.baja.SolicitudBajaHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.observers.SolicitudBajaObserver;
import ar.edu.utn.frba.dds.dominio.solicitudes.procesador.ProcesadorDeSolicitudesDeBaja;

public class DetectorSpamObserver implements SolicitudBajaObserver {
  private final DetectorDeSpam detectorDeSpam;

  public DetectorSpamObserver(DetectorDeSpam detectorDeSpam) {
    this.detectorDeSpam = detectorDeSpam;
  }

  @Override
  public void solicitudBajaCreada(
      SolicitudBajaHecho solicitudBajaHecho, ProcesadorDeSolicitudesDeBaja procesador
  ) {
    if (detectorDeSpam.esSpam(solicitudBajaHecho.getDescripcion())) {
      procesador.rechazar(solicitudBajaHecho);
    }
  }
}
