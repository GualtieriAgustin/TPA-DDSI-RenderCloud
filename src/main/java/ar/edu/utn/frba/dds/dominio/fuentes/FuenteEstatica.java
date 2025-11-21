package ar.edu.utn.frba.dds.dominio.fuentes;

import ar.edu.utn.frba.dds.dominio.archivos.LectorArchivo;
import ar.edu.utn.frba.dds.dominio.archivos.LectorArchivoCsv;
import ar.edu.utn.frba.dds.dominio.fuentes.cache.FuenteCacheable;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.geo.BuscadorDeProvincias;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("ESTATICA")
public class FuenteEstatica extends FuenteCacheable {
  @Transient
  private final Origen origen;
  @Transient
  private LectorArchivo lector;
  @Column(name = "path_archivo")
  private final String pathArchivo;
  @Column(name = "fecha_carga")
  private LocalDateTime fechaCarga = LocalDateTime.now();

  public FuenteEstatica(LectorArchivo lector) {
    super();
    this.lector = lector;
    this.pathArchivo = lector.getFileName();
    this.origen = Origen.DATASET;
  }

  public FuenteEstatica(String pathArchivo) {
    super();
    this.pathArchivo = pathArchivo;
    this.origen = Origen.DATASET;
    this.lector = new LectorArchivoCsv(pathArchivo, fechaCarga);
  }

  public FuenteEstatica(String pathArchivo, String pathGeoJson) {
    super();
    this.pathArchivo = pathArchivo;
    this.origen = Origen.DATASET;
    this.lector = new LectorArchivoCsv(
        pathArchivo,
        new BuscadorDeProvincias(pathGeoJson),
        fechaCarga
    );
  }

  @Override
  public List<Hecho> getHechos() {
    return eliminarDuplicados(this.lector.leerHechos());
  }

  @Override
  public List<Hecho> getHechosPaginado(int pagina, int cantidad) {
    return eliminarDuplicados(lector.leerHechos(pagina, cantidad));
  }

  public Origen getOrigen() {
    return origen;
  }

  public String getDetalleOrigen() {
    return lector.getFileName();
  }

  private List<Hecho> eliminarDuplicados(List<Hecho> hechos) {
    return hechos.stream()
        .collect(
            java.util.stream.Collectors.toMap(
                Hecho::getTitulo, // título como clave
                hecho -> hecho,  // Hecho como valor
                (hecho1, hecho2) -> hecho2, // En caso de conflicto, mantener el último
                java.util.HashMap::new
            )
        )
        .values()
        .stream()
        .toList();
  }

  public void setFechaCarga(LocalDateTime fechaCarga) {
    this.fechaCarga = fechaCarga;
  }

  public FuenteEstatica() {
    // JPA requiere un constructor vacío
    this.origen = Origen.DATASET;
    this.lector = null;
    this.pathArchivo = null;
  }

  @PostLoad
  public void inicializarLector() {
    if (this.pathArchivo != null) {
      this.lector = new LectorArchivoCsv("csv/" + this.pathArchivo, this.fechaCarga);
    }
  }

  public String getPathArchivo() {
    return pathArchivo;
  }

  public String getFechaCarga() {
    return fechaCarga.toString();
  }
}
