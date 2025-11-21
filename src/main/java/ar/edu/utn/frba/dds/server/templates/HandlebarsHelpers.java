package ar.edu.utn.frba.dds.server.templates;

import com.github.jknack.handlebars.Helper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class HandlebarsHelpers {

  public static Helper<LocalDateTime> formatDate() {
    return (localDateTime, options) -> {
      if (localDateTime == null) {
        return "";
      }
      String pattern = options.param(0, "dd/MM/yyyy HH:mm");
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
      return formatter.format(localDateTime);
    };
  }

  public static Helper<Object> eq() {
    return (context, options) -> Objects.equals(context, options.param(0));
  }

  public static Helper<Object> json() {
    return (context, options) ->
        TemplatingUtils.objectMapper.writeValueAsString(context);
  }

}
