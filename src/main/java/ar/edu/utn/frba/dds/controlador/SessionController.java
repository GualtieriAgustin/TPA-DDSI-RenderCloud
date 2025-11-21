package ar.edu.utn.frba.dds.controlador;

import ar.edu.utn.frba.dds.dominio.estadisticas.EstadisticasServiceImpl;
import ar.edu.utn.frba.dds.dominio.estadisticas.ExportadorCsv;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeColecciones;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeUsuarios;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitud;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.javalin.http.Context;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class SessionController {
  RepositorioDeUsuarios repositorioDeUsuarios;
  RepositorioDeColecciones repositorioDeColecciones;
  RepositorioSolicitud repositorioSolicitud;
  EstadisticasServiceImpl servicioDeEstadisticas;
  ExportadorCsv csvMaker;

  public SessionController(RepositorioDeUsuarios repositorioDeUsuarios,
                           RepositorioDeColecciones repositorioDeColecciones,
                           RepositorioSolicitud repositorioSolicitud,
                           EstadisticasServiceImpl servicioDeEstadisticas,
                           ExportadorCsv csvMaker) {
    this.repositorioDeUsuarios = repositorioDeUsuarios;
    this.repositorioDeColecciones = repositorioDeColecciones;
    this.repositorioSolicitud = repositorioSolicitud;
    this.servicioDeEstadisticas = servicioDeEstadisticas;
    this.csvMaker = csvMaker;
  }

  public void showLogin(@NotNull Context context) {
    var model = new HashMap<String, Object>();
    String redirect = context.sessionAttribute("redirectAfterLogin");
    if (redirect != null && !redirect.isEmpty()) {
      model.put("redirectUrl", redirect);
    }

    if (context.sessionAttribute("username") != null) {
      context.redirect("/user");
      return;
    }
    context.render("login.hbs", model);
  }

  public void showUser(@NotNull Context context) {
    String username = context.sessionAttribute("username");
    if (username == null) {
      context.redirect("/login");
      return;
    }

    Usuario usuario = repositorioDeUsuarios.buscarUsuario(username);
    if (usuario == null) {
      context.redirect("/login");
      return;
    }

    // Pasamos los datos del usuario al template
    Map<String, Object> model = new HashMap<>();
    model.put("fullname", usuario.getNombre());
    model.put("email", usuario.getEmail());
    model.put("username", usuario.getUsername());
    model.put("admin", usuario.getAdmin());

    String referer = context.header("Referer");
    String currentUrl = context.fullUrl();

    if (referer == null || Objects.equals(referer, currentUrl) || referer.contains("/login")) {
      referer = "/";
    }
    model.put("backUrl", referer);


    context.render("user-panel.hbs", model);
  }

  public void logout(@NotNull Context context) {
    context.sessionAttribute("username", null);
    context.redirect("/");
  }

  public void showRegister(@NotNull Context context) {
    context.render("register.hbs");
  }


  public void login(@NotNull Context context) {
    try {
      String username = context.formParam("username");
      String password = context.formParam("passwordHash");

      Usuario usuario = repositorioDeUsuarios.buscarUsuario(username);

      if (usuario != null && password != null && usuario.matchPassword(password)) {
        context.sessionAttribute("username", username);
        String redirectUrl = context.formParam("redirect");
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
          context.sessionAttribute("redirectAfterLogin", null);
          context.redirect(redirectUrl);
        } else {
          context.redirect("/");
        }
        return;
      }
      // Si no coincide ninguna condición
      Map<String, String> error = Map.of("error", "Usuario o contraseña incorrectos");
      context.status(400).render("login.hbs", error);

    } catch (Exception e) {
      Map<String, String> error = Map.of("error", "Error al procesar el login");
      context.status(400).render("login.hbs", error);
    }
  }

  public void register(@NotNull Context context) {
    try {
      String fullname = context.formParam("fullname");
      String email = context.formParam("email");
      String username = context.formParam("username");
      String password = context.formParam("passwordHash");


      Argon2 argon2 = Argon2Factory.create();
      String hashedPassword = argon2.hash(2, 65536, 1, password.toCharArray());
      // (2 iteraciones, 64 MB de memoria, 1 thread)
      repositorioDeUsuarios.withTransaction(() ->
              repositorioDeUsuarios.registrar(
                      new Usuario(fullname, hashedPassword, email, username)));

      Map<String, String> info = Map.of("info", "Usuario registrado exitosamente");
      context.status(200).render("login.hbs", info);

    } catch (Exception e) {
      Map<String, String> error = Map.of("error", "No se pudo registrar el usuario");
      context.status(400).render("register.hbs", error);
    }
  }

  public void showAdmin(@NotNull Context context) {
    if (context.sessionAttribute("username") == null) {
      context.redirect("/login");
    } else {
      Usuario usuario = repositorioDeUsuarios.buscarUsuario(context.sessionAttribute("username"));
      if (usuario.getAdmin()) {
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("colecciones", repositorioDeColecciones.consultarTodas());
        modelo.put("solicitudes", repositorioSolicitud.consultarPendientes());
        context.render("admin-panel.hbs", modelo);
      } else {
        context.redirect("/user");
      }
    }
  }
  
  public void generarEstadisticas(@NotNull Context ctx) {

    String fechaInicioStr = ctx.formParam("fechaInicio");
    String fechaFinStr = ctx.formParam("fechaFin");

    // Validación simple
    if (fechaInicioStr == null || fechaFinStr == null) {
      ctx.status(400).result("Faltan parámetros de fecha");
      return;
    }

    LocalDateTime fechaInicio = LocalDateTime.parse(fechaInicioStr);
    LocalDateTime fechaFin = LocalDateTime.parse(fechaFinStr);


    servicioDeEstadisticas.generarEstadisticas(fechaInicio, fechaFin);
    var estadisticas = servicioDeEstadisticas.obtenerUltimaEstadistica();

    Map<String, Object> model = new HashMap<>();
    model.put("colecciones", repositorioDeColecciones.consultarTodas());
    model.put("solicitudes", repositorioSolicitud.consultarPendientes());
    model.put("estadisticas", estadisticas);
    ctx.render("admin-panel.hbs", model);
  }

  public void imprimirEstadisticas(@NotNull Context ctx) {

    String csv = csvMaker.generarReporteCsv();

    ctx.header("Content-Disposition", "attachment; filename=reporte.csv");
    ctx.result(csv);
    ctx.contentType("text/csv; charset=utf-8");
  }

  public void mostrarSolicitudes(@NotNull Context context) {
    if (context.sessionAttribute("username") == null) {
      context.redirect("/login");
    } else {
      Usuario usuario = repositorioDeUsuarios.buscarUsuario(context.sessionAttribute("username"));
      if (usuario.getAdmin()) {
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("solicitudes", repositorioSolicitud.consultarTodas());
        context.render("solicitudes-panel.hbs", modelo);
      } else {
        context.redirect("/user");
      }
    }
  }
}
