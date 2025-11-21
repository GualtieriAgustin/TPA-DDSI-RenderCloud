package ar.edu.utn.frba.dds.dominio.hechos.multimedia;

import java.io.InputStream;

/**
 * Define el contrato para un servicio de almacenamiento de archivos.
 * Permite abstraer la lógica de guardado (local, S3, etc.).
 */
public interface AlmacenamientoDeArchivos {

  String guardar(InputStream inputStream, String nombreArchivoOriginal);

  String getUrlPublica(String url);
}