package ar.edu.utn.frba.dds.dominio.hechos.exceptions;

public class HechoOrigenNoCreadoPorUsuarioException extends RuntimeException {
  public HechoOrigenNoCreadoPorUsuarioException() {
    super("El hecho debe tener origen CONTRIBUYENTE para ser creado por un usuario.");
  }
}
