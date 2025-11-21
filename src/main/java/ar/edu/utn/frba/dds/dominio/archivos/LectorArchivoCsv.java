package ar.edu.utn.frba.dds.dominio.archivos;

import ar.edu.utn.frba.dds.dominio.fuentes.Origen;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import ar.edu.utn.frba.dds.dominio.hechos.geo.BuscadorDeProvincias;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class LectorArchivoCsv implements LectorArchivo {
  private final String pathArchivo;
  private LocalDateTime fechaCarga;

  // TODO: hasta definir si los estaticos tienen id
  private static final Random RANDOM = new Random();

  private BuscadorDeProvincias buscadorDeProvincias;

  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm:ss");

  public LectorArchivoCsv(String pathArchivo, LocalDateTime fechaCarga) {
    this.pathArchivo = Objects.requireNonNull(pathArchivo, "El pathArchivo no puede ser null");
  }

  public LectorArchivoCsv(
      String pathArchivo,
      BuscadorDeProvincias buscadorDeProvincias,
      LocalDateTime fechaCarga
  ) {
    this(pathArchivo, fechaCarga);
    this.buscadorDeProvincias = buscadorDeProvincias;
    this.fechaCarga = fechaCarga;
  }

  @Override
  public List<Hecho> leerHechos() {
    // Al pasar null y -1, procesamos todos los registros sin paginación.
    return procesarCsv(null, -1);
  }

  @Override
  public List<Hecho> leerHechos(int pagina, int cantidad) {
    // La paginación en streams es base 0, pero nuestra `pagina` es base 1.
    long offset = (long) (pagina - 1) * cantidad;
    return procesarCsv(offset, cantidad);
  }

  private Optional<Hecho> mapearRegistro(CSVRecord record) {
    try {
      // Obtenemos los campos del registro. Apache Commons CSV ya maneja las comillas.
      String titulo = record.get(0);
      String descripcion = record.get(1);
      String categoriaTexto = record.get(2);
      double latitud = Double.parseDouble(record.get(3));
      double longitud = Double.parseDouble(record.get(4));
      LocalDateTime fechaSuceso;
      //TODO: Si no viene la hora, ver si le ponemos una por default o lo descartamos.
      try {
        // Intenta parsear como fecha completa
        fechaSuceso = LocalDateTime.parse(record.get(5), formatter);
      } catch (DateTimeParseException e) {
        // Si no viene en el formato esperado lo ignoramos
        return Optional.empty();
      }

      Ubicacion ubicacion = new Ubicacion(latitud, longitud);
      Provincia provincia = buscadorDeProvincias != null
          ? buscadorDeProvincias.buscarProvinciaPorCoordenadas(latitud, longitud) :
          Provincia.PROVINCIA_DESCONOCIDA;

      Hecho hecho = new Hecho(
          titulo,
          descripcion,
          categoriaTexto,
          ubicacion,
          fechaSuceso,
          Origen.DATASET,
          fechaCarga != null ? fechaCarga : LocalDateTime.now(),
          provincia
      );
      hecho.setId(RANDOM.nextLong()); // TODO: hasta definir si los estaticos tienen id
      return Optional.of(hecho);
    } catch (RuntimeException e) {
      throw new MapeoCsvEnHechoException(
          "Error procesando registro del CSV: " + record + ". Causa: " + e.getMessage());
    }
  }

  public String getFileName() {
    if (pathArchivo.contains("/")) {
      return pathArchivo.substring(pathArchivo.lastIndexOf('/') + 1);
    }
    return pathArchivo;
  }

  private InputStream getInputStream() {
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(this.pathArchivo);
    if (inputStream == null) {
      throw new IllegalArgumentException(
          "No se pudo encontrar el archivo en resources: " + this.pathArchivo
      );
    }
    return inputStream;
  }

  private List<Hecho> procesarCsv(Long offset, int limit) {
    try (InputStream is = getInputStream();
         Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
         CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';')
             .withFirstRecordAsHeader() // La primera línea es la cabecera y se ignora
             .withTrim())) { // Elimina espacios en blanco de los campos

      Stream<CSVRecord> stream = csvParser.stream();

      // Aplicamos paginación si es necesario
      if (offset != null) {
        stream = stream.skip(offset);
      }
      if (limit > -1) {
        stream = stream.limit(limit);
      }

      // Mapeamos cada registro a un Hecho y recolectamos
      return stream.map(this::mapearRegistro)
          .flatMap(Optional::stream)
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new RuntimeException("Error al procesar el archivo CSV: " + pathArchivo, e);
    }
  }
}
