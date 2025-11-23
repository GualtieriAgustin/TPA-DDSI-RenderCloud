package ar.edu.utn.frba.dds.dominio.hechos.multimedia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

public class AlmacenamientoEnCloudFare implements AlmacenamientoDeArchivos {

    private static final Logger logger =
            LoggerFactory.getLogger(AlmacenamientoEnCloudFare.class);

    private final S3Client s3;
    private final String bucketName;
    private final String carpeta;
    private final String publicBaseUrl;

    public AlmacenamientoEnCloudFare(String bucketName, String carpeta) {

        this.bucketName = bucketName;
        this.carpeta = carpeta;

        String endpoint = System.getenv("R2_ENDPOINT");
        String accessKey = System.getenv("R2_ACCESS_KEY");
        String secretKey = System.getenv("R2_SECRET_KEY");

        this.publicBaseUrl =
                "https://pub-da6b9ed3f7c3454a96ca3e92385c0b23.r2.dev";

        this.s3 = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();

        logger.info("Servicio de almacenamiento en Cloudflare R2 inicializado.");
    }

    @Override
    public String guardar(InputStream inputStream, String nombreArchivoOriginal) {
        String extension = obtenerExtension(nombreArchivoOriginal);
        String mimeType = obtenerMimeType(extension);

        String nombreUnico = carpeta + "/" + UUID.randomUUID() + "." + extension;

        try {
            byte[] bytes = inputStream.readAllBytes();

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(nombreUnico)
                    .contentType(mimeType)
                    .build();

            s3.putObject(request, RequestBody.fromBytes(bytes));

            logger.info("Archivo subido a Cloudflare R2: {}", nombreUnico);

            return nombreUnico;

        } catch (IOException e) {
            throw new RuntimeException("Error al leer archivo para subir a R2", e);
        }
    }

    @Override
    public String getUrlPublica(String url) {
        return publicBaseUrl + "/" + url;
    }

    private String obtenerExtension(String nombreArchivo) {
        return (nombreArchivo != null && nombreArchivo.contains("."))
                ? nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1)
                : "";
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