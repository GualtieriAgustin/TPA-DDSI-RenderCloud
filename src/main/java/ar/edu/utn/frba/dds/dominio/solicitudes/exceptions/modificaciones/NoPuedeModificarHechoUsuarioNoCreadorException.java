package ar.edu.utn.frba.dds.dominio.solicitudes.exceptions.modificaciones;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;

public class NoPuedeModificarHechoUsuarioNoCreadorException extends RuntimeException {
  public NoPuedeModificarHechoUsuarioNoCreadorException(Hecho hecho) {
    super("No puede modificar hecho. El usuario no creó el hecho." + hecho.getTitulo());
  }
}