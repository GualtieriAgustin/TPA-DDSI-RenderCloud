package ar.edu.utn.frba.dds.dominio.hechos.exceptions;

public class HechoNoCreadoPorUsuarioException extends RuntimeException {
  public HechoNoCreadoPorUsuarioException() {
    super("El hecho debe ser un credo por usuario.");
  }
}
