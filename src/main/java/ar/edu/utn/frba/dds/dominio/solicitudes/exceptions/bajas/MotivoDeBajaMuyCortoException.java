package ar.edu.utn.frba.dds.dominio.solicitudes.exceptions.bajas;

/**
 * Los motivos para bajar un hecho deben ser bien fundados con un mínimo de 500 caracteres.
 */
public class MotivoDeBajaMuyCortoException extends RuntimeException {
  public MotivoDeBajaMuyCortoException() {
    super("Se requiere una descripción de al menos 500 caracteres para dar de baja un hecho.");
  }
}
