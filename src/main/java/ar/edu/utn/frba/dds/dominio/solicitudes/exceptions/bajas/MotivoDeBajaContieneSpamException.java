package ar.edu.utn.frba.dds.dominio.solicitudes.exceptions.bajas;

public class MotivoDeBajaContieneSpamException extends RuntimeException {
  public MotivoDeBajaContieneSpamException(String descripcion) {
    super("No se puede crear la solicitud de baja porque el motivo contiene spam: "
        + descripcion);
  }
}
