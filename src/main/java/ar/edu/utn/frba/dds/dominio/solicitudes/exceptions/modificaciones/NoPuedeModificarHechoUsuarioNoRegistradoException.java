package ar.edu.utn.frba.dds.dominio.solicitudes.exceptions.modificaciones;

public class NoPuedeModificarHechoUsuarioNoRegistradoException extends RuntimeException {
  public NoPuedeModificarHechoUsuarioNoRegistradoException() {
    super("No puede modificar hechos. El usuario no está registrado.");
  }

}
