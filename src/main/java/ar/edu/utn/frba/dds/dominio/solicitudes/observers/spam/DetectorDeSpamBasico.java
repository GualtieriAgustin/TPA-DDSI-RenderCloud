package ar.edu.utn.frba.dds.dominio.solicitudes.observers.spam;

public class DetectorDeSpamBasico implements DetectorDeSpam {

  public boolean esSpam(String descripcionSolicitud) {
    return descripcionSolicitud.contains("spam");
  }

}
