package ar.edu.utn.frba.dds.server;

import ar.edu.utn.frba.dds.controlador.ColeccionesController;
import ar.edu.utn.frba.dds.controlador.FuentesController;
import ar.edu.utn.frba.dds.controlador.HealthCheckController;
import ar.edu.utn.frba.dds.controlador.HechosController;
import ar.edu.utn.frba.dds.controlador.ProvinciasController;
import ar.edu.utn.frba.dds.controlador.SessionController;
import ar.edu.utn.frba.dds.controlador.SolicitudesController;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import io.javalin.Javalin;
import java.util.List;

public class Router implements SimplePersistenceTest {
  private final List<String> protectedPaths = List.of(
      "/solicitudes/formulario",
      "/user/admin"
  );

  public void configure(
      Javalin app,
      HechosController hechosController,
      ProvinciasController provinciasController,
      SolicitudesController solicitudesController,
      HealthCheckController healthCheckController,
      SessionController sessionController,
      FuentesController fuentesController,
      ColeccionesController coleccionesController
  ) {
    configureBefores(app);

    // Hechos
    app.get("/", ctx -> ctx.render("home.hbs", hechosController.renderizarHechos(ctx)));
    app.get("/hechos/formulario", hechosController::mostrarFormulario);
    app.get("/hechos/{id}", hechosController::obtenerHecho);
    app.post("/hechos", hechosController::subirHecho);
    app.get("/hechos", hechosController::obtenerHechosJson);

    // Solicitudes
    app.get("/solicitudes/formulario", solicitudesController::mostrarFormulario);
    app.post("/solicitudes", solicitudesController::crearSolicitud);
    app.get("/user/admin/solicitudes", sessionController::mostrarSolicitudes);
    app.post("/admin/solicitudes/{id}/aprobar", solicitudesController::aprobar);
    app.post("/admin/solicitudes/{id}/rechazar", solicitudesController::rechazar);

    // Utils
    app.get("/healthcheck", context -> context.json(healthCheckController.healtcheck()));
    app.get("/provincia/buscar", ctx -> ctx.json(provinciasController.buscarProvincia(ctx)));
    app.get("/provincias", ctx -> ctx.json(provinciasController.listar()));

    //Login
    app.get("/login", sessionController::showLogin);
    app.post("/login", sessionController::login);
    // Register
    app.get("/register", sessionController::showRegister);
    app.post("/register", sessionController::register);
    // Panel de usuario
    app.get("/user", sessionController::showUser);
    app.get("/logout", sessionController::logout);

    //admin
    app.get("/user/admin", sessionController::showAdmin);
    app.post("/user/admin/obtenerEstadisticas", sessionController::generarEstadisticas);
    app.get("/user/admin/guardarEstadisticas", sessionController::imprimirEstadisticas);

    // Fuentes
    app.get("/user/admin/fuentes", fuentesController::showFuentes);

    // Colecciones
    app.get("/user/admin/colecciones", coleccionesController::showColecciones);
    app.get("/user/admin/colecciones/formulario", coleccionesController::mostrarFormulario);
    app.post("/user/admin/colecciones", coleccionesController::subirColeccion);

    //Crons

    app.post("/cron/generarEstadisticas", ctx -> {
      try {
        String token = ctx.header("X-CRON-TOKEN");
        if (!"mi-super-token".equals(token)) {
          ctx.status(401).result("Unauthorized");
          return;
        }
        sessionController.generarEstadisticas(ctx);
        ctx.result("OK");
      } catch (Exception e) {
        e.printStackTrace();
        ctx.status(500).result("ERROR");
      }
    });

    app.post("/cron/renderizarHechos", ctx -> {
      try {
        String token = ctx.header("X-CRON-TOKEN");
        if (!"mi-super-token".equals(token)) {
          ctx.status(401).result("Unauthorized");
          return;
        }
        hechosController.renderizarHechos(ctx);
        ctx.result("OK");
      } catch (Exception e) {
        e.printStackTrace();
        ctx.status(500).result("ERROR");
      }
    });

    app.post("/cron/calcular-consenso", ctx -> {
      String token = ctx.header("X-CRON-TOKEN");
      if (!"mi-super-token".equals(token)) {
        ctx.status(401).result("Unauthorized");
        return;
      }

      //CalcularConsenso.calcular();
      ctx.result("OK");
    });
  }

  private void configureBefores(Javalin app) {
    app.before(ctx -> entityManager().clear());

    app.before(ctx -> ctx.header("Content-Type", "text/html; charset=UTF-8"));

    app.before(ctx -> {
      String path = ctx.path();
      boolean requiereAuth = this.protectedPaths.contains(path);

      if (requiereAuth && ctx.sessionAttribute("username") == null) {
        String redirectUrl = ctx.fullUrl();
        ctx.sessionAttribute("redirectAfterLogin", redirectUrl);
        ctx.redirect("/login");
      }
    });

    app.before(ctx -> {
      String csp = "default-src 'self';"
          + " script-src 'self' 'unsafe-inline';"
          + " style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://code.jquery.com;"
          + " img-src 'self' data: https://wms.ign.gob.ar  https://media.metamapa.com.ar https://pub-da6b9ed3f7c3454a96ca3e92385c0b23.r2.dev;"
          + " media-src 'self' https://media.metamapa.com.ar https://pub-da6b9ed3f7c3454a96ca3e92385c0b23.r2.dev;"
          + " font-src 'self' https://cdn.jsdelivr.net https://pub-da6b9ed3f7c3454a96ca3e92385c0b23.r2.dev;"
          + " object-src 'none';"
          + " frame-ancestors 'none';"
          + " form-action 'self';"
          + " base-uri 'self';";
      ctx.header("Content-Security-Policy", csp);
    });
  }
}