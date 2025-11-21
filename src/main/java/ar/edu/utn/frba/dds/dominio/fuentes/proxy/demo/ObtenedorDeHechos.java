package ar.edu.utn.frba.dds.dominio.fuentes.proxy.demo;

import ar.edu.utn.frba.dds.dominio.fuentes.Origen;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObtenedorDeHechos {
  private static final Logger logger = LoggerFactory.getLogger(ObtenedorDeHechos.class);
  private final URL url;
  private final Conexion conexion;

  public ObtenedorDeHechos(String url, Conexion conexion) {
    try {
      this.url = new URL(url);
    } catch (MalformedURLException e) {
      throw new RuntimeException(url + " no tiene un formato valido", e);
    }
    this.conexion = conexion;
  }

  public List<Hecho> getNuevosHechos(LocalDateTime partiendoDeEstaFecha) {
    List<Hecho> nuevosHechos = new ArrayList<>();

    try {
      while (true) {
        Map<String, Object> hechosMap = conexion.siguienteHecho(url, partiendoDeEstaFecha);

        if (hechosMap == null) {
          logger.debug("No hay más hechos de {} para esta tanda.", url);
          break;
        }

        Hecho nuevoHecho = convertirEnHecho(hechosMap);
        nuevosHechos.add(nuevoHecho);
      }

      if (!nuevosHechos.isEmpty()) {
        logger.debug("Agregados {} hechos nuevos de {}", nuevosHechos.size(), url);
      }

      logger.debug("Actualización exitosa para {}. Fecha de la última consulta: {}",
          url, partiendoDeEstaFecha);
    } catch (RuntimeException e) {
      //Si falla la pegada al servicio externo queremos saber por qué
      logger.error("Error al procesar fuente demo para {}: {}", url, e);
    }

    return nuevosHechos;
  }

  public String getUrl() {
    return url.toString();
  }

  // Implementación ficticia
  private Hecho convertirEnHecho(Map<String, Object> hechosMap) {
    String titulo = hechosMap.getOrDefault("titulo", "titulo").toString();
    String descripcion = hechosMap.getOrDefault("descripcion", "desc").toString();
    Origen origen = Origen.PROXY;
    LocalDateTime fechaCarga = LocalDateTime.now();

    return new Hecho(
        titulo,
        descripcion,
        "categoria",
        new Ubicacion(1.0, 1.0),
        LocalDateTime.now(),
        origen,
        fechaCarga,
        Provincia.PROVINCIA_DESCONOCIDA
    );
  }

}
