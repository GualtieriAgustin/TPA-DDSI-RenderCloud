package ar.edu.utn.frba.dds.dominio.hechos.multimedia;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class AlmacenamientoLocal implements AlmacenamientoDeArchivos {

  private final Path baseDirectorio;

  public AlmacenamientoLocal(String directorio) {
    this.baseDirectorio = Paths.get(directorio);
    try {
      Files.createDirectories(baseDirectorio);
    } catch (IOException e) {
      throw new RuntimeException("No se pudo crear el directorio de subidas", e);
    }
  }

  @Override
  public String guardar(InputStream inputStream, String nombreArchivoOriginal) {
    String extension = obtenerExtension(nombreArchivoOriginal);
    String nombreUnico = UUID.randomUUID() + "." + extension;
    Path destino = this.baseDirectorio.resolve(nombreUnico);

    try (InputStream is = inputStream) {
      Files.copy(is, destino, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Error al guardar el archivo", e);
    }

    return nombreUnico;
  }

  private String obtenerExtension(String nombreArchivo) {
    if (nombreArchivo == null || !nombreArchivo.contains(".")) {
      return "";
    }
    return nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1);
  }

  @Override
  public String getUrlPublica(String nombre) {
    return baseDirectorio + "/" + nombre;
  }
}