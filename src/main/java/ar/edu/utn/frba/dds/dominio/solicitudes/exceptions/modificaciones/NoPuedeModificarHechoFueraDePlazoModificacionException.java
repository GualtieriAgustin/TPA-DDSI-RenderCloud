package ar.edu.utn.frba.dds.dominio.solicitudes.exceptions.modificaciones;

public class NoPuedeModificarHechoFueraDePlazoModificacionException extends RuntimeException {
  public NoPuedeModificarHechoFueraDePlazoModificacionException() {
    super("No puede modificar hechos. Hecho creado en un plazo posterior de una semana.");
  }
}