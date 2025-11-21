package ar.edu.utn.frba.dds.dominio.hechos.multimedia;

public enum TipoMultimedia {
  IMAGEN,
  VIDEO;

  public static TipoMultimedia fromContentType(String contentType) {
    if (contentType != null && contentType.startsWith("video")) {
      return VIDEO;
    }
    return IMAGEN; // Por defecto, si no es video, es imagen.
  }
}