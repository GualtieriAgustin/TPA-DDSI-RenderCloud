package ar.edu.utn.frba.dds.dominio.archivos;

public class MapeoCsvEnHechoException extends RuntimeException {
  public MapeoCsvEnHechoException(String message) {
    super("Hubo un error en el mapeo de lineas de CSV a Hechos: " + message);
  }
}
