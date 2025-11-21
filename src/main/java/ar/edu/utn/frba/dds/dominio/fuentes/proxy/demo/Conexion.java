package ar.edu.utn.frba.dds.dominio.fuentes.proxy.demo;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Hasta donde sabemos, no existe ninguna fuente externa que tenga esta interfaz.
 * La misma es sólo a modo de ejemplo y para poner nuestro diseño a prueba.
 */
public interface Conexion {
  /**
   * Devuelve un mapa con los atributos de un hecho, indexados por nombre de
   * atributo. Si el método retorna null, significa que no hay nuevos hechos
   * por ahora. La fecha es opcional
   */
  Map<String, Object> siguienteHecho(URL url, LocalDateTime fechaUltimaConsulta);
}