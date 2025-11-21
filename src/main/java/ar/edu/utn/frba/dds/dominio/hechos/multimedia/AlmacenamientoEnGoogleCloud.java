package ar.edu.utn.frba.dds.dominio.hechos.multimedia;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlmacenamientoEnGoogleCloud implements AlmacenamientoDeArchivos {

  private static final Logger logger = LoggerFactory.getLogger(AlmacenamientoEnGoogleCloud.class);
  private final Storage storage;
  private final String bucketName;
  private final String carpeta;

  public AlmacenamientoEnGoogleCloud(String bucketName, String carpeta) {
    // La autenticación se maneja automáticamente si la aplicación corre en un entorno de GCP
    // (como una VM, Cloud Run, etc.) o si tenés configurado 'gcloud auth application-default login'
    // en tu entorno local.
    this.storage = StorageOptions.getDefaultInstance().getService();
    this.bucketName = bucketName;
    this.carpeta = carpeta;
    logger.info(
        "Servicio de almacenamiento en Google Cloud Storage "
            + "inicializado para el bucket: {}", bucketName
    );
  }

  @Override
  public String guardar(InputStream inputStream, String nombreArchivoOriginal) {
    String extension = obtenerExtension(nombreArchivoOriginal);
    String mimeType = obtenerMimeType(extension);
    String nombreUnico = carpeta + "/" + UUID.randomUUID() + "." + extension;

    BlobId blobId = BlobId.of(bucketName, nombreUnico);
    BlobInfo blobInfo = BlobInfo
        .newBuilder(blobId)
        .setContentType(mimeType)
        .build();

    try {
      // Sube el archivo a GCS
      storage.create(blobInfo, inputStream.readAllBytes());
      logger.info("Archivo subido a GCS: {}/{}", bucketName, nombreUnico);

      return nombreUnico;
    } catch (IOException e) {
      throw new RuntimeException("Error al leer el stream del archivo para subir a GCS", e);
    }
  }

  @Override
  public String getUrlPublica(String url) {
    return "https://media.metamapa.com.ar/" + url;
  }

  private String obtenerExtension(String nombreArchivo) {
    return (nombreArchivo != null && nombreArchivo.contains("."))
        ? nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1) : "";
  }

  private String obtenerMimeType(String extension) {
    return switch (extension.toLowerCase()) {
      case "jpg", "jpeg" -> "image/jpeg";
      case "png" -> "image/png";
      case "gif" -> "image/gif";
      case "webp" -> "image/webp";
      case "mp4" -> "video/mp4";
      case "mov" -> "video/quicktime";
      case "webm" -> "video/webm";
      default -> "application/octet-stream";
    };
  }
}