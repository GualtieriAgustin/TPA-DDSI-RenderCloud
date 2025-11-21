package ar.edu.utn.frba.dds.dominio.solicitudes.observers.spam;

public interface DetectorDeSpam {

  /**
   * Detecta si una solicitud es spam.
   *
   * @param descripcionSolicitud la descripción de la solicitud a evaluar
   * @return true si la solicitud es spam, false en caso contrario
   */
  boolean esSpam(String descripcionSolicitud);
}
