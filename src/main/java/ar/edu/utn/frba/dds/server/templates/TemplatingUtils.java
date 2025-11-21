package ar.edu.utn.frba.dds.server.templates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.text.SimpleDateFormat;
import java.util.Map;

public class TemplatingUtils {
  public static final ObjectMapper objectMapper = customObjectMapper();

  public static Map<String, Object> errorMessage(String mensaje, int codigo) {
    return Map.of(
        "mensaje", mensaje,
        "codigo", codigo,
        "type", "error"
    );
  }

  private static ObjectMapper customObjectMapper() {
    var customMapper = new ObjectMapper();
    customMapper.registerModule(new JavaTimeModule());
    customMapper.setDateFormat(new SimpleDateFormat());
    return customMapper;
  }
}
