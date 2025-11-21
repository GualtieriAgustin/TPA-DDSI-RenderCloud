package ar.edu.utn.frba.dds.controlador;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroHecho;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroProvincia;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroTextoLibre;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.servicio.HechosService;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HechosController {
  private static final Logger logger = LoggerFactory.getLogger(HechosController.class);
  private final HechosService hechosService;

  public HechosController(
      HechosService hechosService
  ) {
    this.hechosService = hechosService;
  }

  public void mostrarFormulario(Context ctx) {
    var model = new HashMap<String, Object>();
    model.put("provincias", Provincia.todas());
    model.put("username", ctx.sessionAttribute("username"));
    ctx.render("subir-hecho.hbs", model);
  }

  public void obtenerHecho(Context ctx) {
    Long hechoId;

    try {
      String idParam = Objects.requireNonNull(ctx.pathParam("id"));
      hechoId = Long.parseLong(idParam);
    } catch (NumberFormatException e) {
      ctx.status(400).json(Map.of("error", "Formato de ID inválido. Debe ser numérico."));
      return;
    }

    Hecho hecho = hechosService.buscarPorId(hechoId);

    if (hecho == null) {
      ctx.status(404).json(Map.of("error", "Hecho no encontrado con ID: " + hechoId));
      return;
    }

    ctx.json(hecho);
  }

  private List<Hecho> buscarHechos(Context ctx) {
    String paginaParam = ctx.queryParam("pagina");
    String cantidadParam = ctx.queryParam("cantidad");
    String limiteParam = ctx.queryParam("limite");
    boolean paginado;
    int pagina;
    int cantidad;

    if (paginaParam != null && cantidadParam != null) {
      pagina = Integer.parseInt(paginaParam);
      cantidad = Integer.parseInt(cantidadParam);
      paginado = true;
    } else if (limiteParam != null) {
      cantidad = Integer.parseInt(limiteParam);
      paginado = false;
      pagina = 1;
    } else {
      cantidad = 50;
      pagina = 1;
      paginado = false;
    }

    List<FiltroHecho> filtros = new ArrayList<>();
    String provinciaParam = ctx.queryParam("provincia");

    if (provinciaParam != null && !provinciaParam.isEmpty()) {
      Provincia provincia = Provincia.valueOf(provinciaParam);
      if (Provincia.PROVINCIA_DESCONOCIDA != provincia) {
        filtros.add(new FiltroProvincia(provincia));
      }
    }

    String textoLibreParam = ctx.queryParam("textoLibre");
    if (textoLibreParam != null && !textoLibreParam.isEmpty()) {
      filtros.add(new FiltroTextoLibre(textoLibreParam));
    }

    return paginado
        ? hechosService.buscar(filtros, pagina, cantidad)
        : hechosService.buscar(filtros, 1, cantidad);
  }

  public Map<String, Object> renderizarHechos(Context ctx) {
    List<Hecho> hechos = this.buscarHechos(ctx);

    var model = new HashMap<String, Object>();
    model.put("hechos", hechos); // La lista ya está ordenada y limitada
    model.put("username", ctx.sessionAttribute("username"));
    return model;
  }

  public void obtenerHechosJson(Context ctx) {
    long tiempoTotalComienzo = System.currentTimeMillis();
    List<Hecho> obj = this.buscarHechos(ctx);
    long tiempoTotalFin = System.currentTimeMillis();
    ctx.json(
        Map.of(
            "hechos", obj,
            "metadata", Map.of("tiempo", tiempoTotalFin - tiempoTotalComienzo)
        )
    );
  }

  // Idealmente levantados de memoria o vista para velocidad
  public Map<String, List<Hecho>> ultimosHechos() {
    logger.info("Obteniendo hechos recientes...");
    return Map.of("hechos", List.of());
  }

  public void subirHecho(Context ctx) {
    try {
      logger.info("Subiendo hecho...");

      String titulo = Objects.requireNonNull(ctx.formParam("titulo"));
      String descripcion = Objects.requireNonNull(ctx.formParam("descripcion"));
      String categoria = Objects.requireNonNull(ctx.formParam("categoria"));
      Double latitud = Double.parseDouble(Objects.requireNonNull(ctx.formParam("latitud")));
      Double longitud = Double.parseDouble(Objects.requireNonNull(ctx.formParam("longitud")));
      LocalDateTime fechaSuceso = LocalDateTime.parse(
          Objects.requireNonNull(ctx.formParam("fechaSuceso")));
      Provincia provincia = Provincia.valueOf(Objects.requireNonNull(ctx.formParam("provincia")));
      List<UploadedFile> archivosSubidos = ctx.uploadedFiles();
      List<String> descripcionesArchivos = ctx.formParams("multimedia_descripciones");

      String username = ctx.sessionAttribute("username");

      hechosService.crearHecho(titulo, descripcion, categoria, latitud, longitud, fechaSuceso,
          provincia, archivosSubidos, descripcionesArchivos, username);

      ctx.render("feedback-subida.hbs", Map.of("type", "success"));

    } catch (RuntimeException e) {
      logger.error("Error al subir el hecho: {}", e.getMessage());
      ctx.render("feedback-subida.hbs",
          Map.of("mensaje", "Ocurrió un error al procesar la solicitud.\n" + e.getMessage(),
              "type", "error"));
    }
  }
}
