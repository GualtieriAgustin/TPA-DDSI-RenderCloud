package ar.edu.utn.frba.dds.dominio.estadisticas.querys;

import ar.edu.utn.frba.dds.dominio.estadisticas.EstadisticaResultado;
import com.google.gson.Gson;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.time.LocalDateTime;

public abstract class EstadisticaQuery implements WithSimplePersistenceUnit {

  protected final String nombreEstadistica;

  public EstadisticaQuery(String nombreEstadistica) {
    this.nombreEstadistica = nombreEstadistica;
  }

  public String getNombre() {
    return nombreEstadistica;
  }

  protected String ejecutarQueryNativa(String sql) {
    var resultado = entityManager()
        .createNativeQuery(sql)
        .getResultList();

    Gson gson = new Gson();
    return gson.toJson(resultado);
  }

  public abstract  EstadisticaResultado generarEstadisticas(
          LocalDateTime fechaInicio, LocalDateTime fechaFin);

  public abstract String ejecutarQuery(LocalDateTime fechaInicio, LocalDateTime fechaFin);

}
