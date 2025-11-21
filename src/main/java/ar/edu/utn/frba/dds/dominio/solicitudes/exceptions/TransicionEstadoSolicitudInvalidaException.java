package ar.edu.utn.frba.dds.dominio.solicitudes.exceptions;


import ar.edu.utn.frba.dds.dominio.solicitudes.EstadoSolicitud;

/**
 * No debemos permitir que una solicitud haga un cambio de estado inválido.
 *
 */
public class TransicionEstadoSolicitudInvalidaException extends RuntimeException {
  public TransicionEstadoSolicitudInvalidaException(EstadoSolicitud inicio, EstadoSolicitud fin) {
    super("No se puede pasar del estado " + inicio + " al estado " + fin);
  }
}
