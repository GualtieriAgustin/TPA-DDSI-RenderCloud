package ar.edu.utn.frba.dds.dominio.fuentes.proxy.demo;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;

public class ConexionHttp implements Conexion {

  // Implementación ficticia
  @Override
  public Map<String, Object> siguienteHecho(URL url, LocalDateTime fechaUltimaConsulta) {
    return null;
  }
}
