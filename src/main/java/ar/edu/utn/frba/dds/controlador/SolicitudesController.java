package ar.edu.utn.frba.dds.controlador;

import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.server.templates.TemplatingUtils;
import ar.edu.utn.frba.dds.servicio.SolicitudesService;
import io.github.flbulgarelli.jpa.extras.TransactionalOps;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolicitudesController implements WithSimplePersistenceUnit, TransactionalOps {

  public static final String SOLICITUD_BAJA_EXITO = "Solicitud de baja creada con éxito.";
  private final SolicitudesService solicitudesService;

  private static final Logger logger = LoggerFactory.getLogger(SolicitudesController.class);

  public SolicitudesController(
      SolicitudesService solicitudesService
  ) {
    this.solicitudesService = solicitudesService;
  }

  public void mostrarFormulario(Context ctx) {
    String titulo = ctx.queryParam("titulo");
    String descripcion = ctx.queryParam("descripcion");
    String provinciaParam = ctx.queryParam("provincia");

    Map<String, Object> model = new HashMap<>();

    model.put("username", ctx.sessionAttribute("username"));

    if (titulo != null && descripcion != null) {
      model.put("titulo_hecho", titulo);
      model.put("descripcion_hecho", descripcion);
    } else {
      ctx.render("error.hbs",
          TemplatingUtils.errorMessage("Falta el título o la descripción del hecho", 400)
      );
      return;
    }

    if (provinciaParam != null) {
      Provincia provincia = Provincia.valueOf(provinciaParam);
      model.put("provincia_hecho", provincia.getNombreGeoJson());
      model.put("provincia_hecho_value", provincia.name());
    } else {
      ctx.render("error.hbs",
          TemplatingUtils.errorMessage("Falta la provincia", 400)
      );
      return;
    }

    ctx.render("solicitudes-hecho.hbs", model);
  }

  public void crearSolicitud(Context ctx) {
    String titulo = ctx.formParam("titulo_hecho");
    String descripcionHecho = ctx.formParam("descripcion_hecho");
    String provinciaParam = ctx.formParam("provincia_hecho_value");
    String justificacion = ctx.formParam("justificacion");
    String tipoSolicitud = ctx.formParam("tipo_solicitud");

    if (titulo == null
        || descripcionHecho == null
        || provinciaParam == null
        || justificacion == null
        || tipoSolicitud == null) {
      mensajeDeError(ctx, "Faltan datos del hecho para crear la solicitud");
      return;
    }

    Provincia provincia = Provincia.valueOf(provinciaParam);

    String username = ctx.sessionAttribute("username");
    if (username == null || username.isEmpty()) {
      mensajeDeError(ctx, "Debe iniciar sesión para crear una solicitud");
      return;
    }

    switch (tipoSolicitud) {
      case "BAJA" -> {
        try {
          solicitudesService.crearSolicitudDeBaja(
              titulo, descripcionHecho, provincia, justificacion, username
          );
        } catch (RuntimeException e) {
          mensajeDeError(ctx, e.getMessage());
          return;
        }

        logger.info(SOLICITUD_BAJA_EXITO);
        ctx.render("feedback-subida.hbs",
            Map.of("type", "success", "mensaje", SOLICITUD_BAJA_EXITO));
      }
      case "MODIFICACION" -> mensajeDeError(ctx, "No se implementó aún");
      default -> mensajeDeError(ctx, "Tipo de solicitud no soportado: " + tipoSolicitud);
    }
  }

  public void aprobar(@NotNull Context ctx) {
    Map<String, Object> modelo = new HashMap<>();

    withTransaction(() -> solicitudesService.aprobarSolicitud(Long.valueOf(ctx.pathParam("id"))));

    modelo.put("solicitudes", solicitudesService.consultarPendientes());
    ctx.render("admin-panel.hbs", modelo);
  }

  public void rechazar(@NotNull Context ctx) {
    Map<String, Object> modelo = new HashMap<>();

    withTransaction(() -> solicitudesService.rechazarSolicitud(Long.valueOf(ctx.pathParam("id"))));

    modelo.put("solicitudes", solicitudesService.consultarPendientes());
    ctx.render("admin-panel.hbs", modelo);
  }

  private void mensajeDeError(Context ctx, String mensaje) {
    ctx.render("feedback-subida.hbs",
        TemplatingUtils.errorMessage(mensaje, 400));
  }
}
