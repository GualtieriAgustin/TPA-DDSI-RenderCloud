package ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GsonConfig {
  public static Gson createGson() {
    return new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
          private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

          @Override
          public void write(JsonWriter out, LocalDateTime value) throws IOException {
            out.value(value.format(formatter));
          }

          @Override
          public LocalDateTime read(JsonReader in) throws IOException {
            return LocalDateTime.parse(in.nextString(), formatter);
          }
        })
        .create();
  }
}
