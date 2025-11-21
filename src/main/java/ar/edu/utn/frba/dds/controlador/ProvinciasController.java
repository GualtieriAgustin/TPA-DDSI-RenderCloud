package ar.edu.utn.frba.dds.controlador;

import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.hechos.geo.BuscadorDeProvincias;
import io.javalin.http.Context;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProvinciasController {
  private final BuscadorDeProvincias buscadorDeProvincias;

  public ProvinciasController(BuscadorDeProvincias buscador) {
    this.buscadorDeProvincias = buscador;
  }

  public List<Map<String, String>> listar() {
    return Provincia.todas();
  }

  public Map<String, String> buscarProvincia(Context ctx) {
    double lat = Double.parseDouble(Objects.requireNonNull(ctx.queryParam("lat")));
    double lon = Double.parseDouble(Objects.requireNonNull(ctx.queryParam("lon")));

    Provincia provincia = buscadorDeProvincias.buscarProvinciaPorCoordenadas(lat, lon);
    return Map.of("value", provincia.name(), "nombre", provincia.getNombreGeoJson());
  }
}
