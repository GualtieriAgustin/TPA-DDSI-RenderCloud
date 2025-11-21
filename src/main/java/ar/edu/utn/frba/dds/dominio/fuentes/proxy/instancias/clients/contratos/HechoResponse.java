package ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos;

import ar.edu.utn.frba.dds.dominio.fuentes.Origen;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.Multimedia;
import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HechoResponse {
  @SerializedName("titulo")
  private String titulo;
  @SerializedName("descripcion")
  private String descripcion;
  @SerializedName("categoria")
  private String categoria;
  @SerializedName("ubicacion")
  private Ubicacion ubicacion;
  @SerializedName("fechaSuceso")
  private LocalDateTime fechaSuceso;
  @SerializedName("fechaCarga")
  private LocalDateTime fechaCarga;
  @SerializedName("multimedia")
  private List<Multimedia> multimedia;

  public HechoResponse(
      String titulo,
      String descripcion,
      String categoria,
      Ubicacion ubicacion,
      LocalDateTime fechaSuceso,
      LocalDateTime fechaCarga,
      List<Multimedia> multimedia) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.ubicacion = ubicacion;
    this.fechaSuceso = fechaSuceso;
    this.fechaCarga = fechaCarga;
    this.multimedia = (multimedia != null) ? new ArrayList<>(multimedia) : new ArrayList<>();
  }

  public Hecho toDomain() {
    return new Hecho(
        this.titulo,
        this.descripcion,
        this.categoria,
        this.ubicacion,
        this.fechaSuceso,
        Origen.PROXY,
        this.fechaCarga,
        this.multimedia,
        Provincia.PROVINCIA_DESCONOCIDA
    );
  }
}
