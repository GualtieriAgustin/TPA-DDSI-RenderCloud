package ar.edu.utn.frba.dds.server;

import ar.edu.utn.frba.dds.controlador.ColeccionesController;
import ar.edu.utn.frba.dds.controlador.FuentesController;
import ar.edu.utn.frba.dds.controlador.HealthCheckController;
import ar.edu.utn.frba.dds.controlador.HechosController;
import ar.edu.utn.frba.dds.controlador.ProvinciasController;
import ar.edu.utn.frba.dds.controlador.SessionController;
import ar.edu.utn.frba.dds.controlador.SolicitudesController;
import ar.edu.utn.frba.dds.dominio.estadisticas.EstadisticasServiceImpl;
import ar.edu.utn.frba.dds.dominio.estadisticas.ExportadorCsv;
import ar.edu.utn.frba.dds.dominio.estadisticas.RepositorioEstadisticas;
import ar.edu.utn.frba.dds.dominio.estadisticas.querys.CategoriaConMasHechos;
import ar.edu.utn.frba.dds.dominio.estadisticas.querys.HoraConMasHechosPorCategoria;
import ar.edu.utn.frba.dds.dominio.estadisticas.querys.ProvinciaConMasHechos;
import ar.edu.utn.frba.dds.dominio.estadisticas.querys.ProvinciaConMasHechosPorCategoria;
import ar.edu.utn.frba.dds.dominio.hechos.geo.BuscadorDeProvincias;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.AlmacenamientoDeArchivos;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.AlmacenamientoEnCloudFare;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.AlmacenamientoEnGoogleCloud;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.AlmacenamientoLocal;
import ar.edu.utn.frba.dds.dominio.solicitudes.observers.SolicitudBajaObserver;
import ar.edu.utn.frba.dds.dominio.solicitudes.observers.spam.DetectorDeSpamBasico;
import ar.edu.utn.frba.dds.dominio.solicitudes.observers.spam.DetectorSpamObserver;
import ar.edu.utn.frba.dds.dominio.solicitudes.procesador.ProcesadorDeSolicitudesDeBaja;
import ar.edu.utn.frba.dds.dominio.solicitudes.procesador.ProcesadorDeSolicitudesDeModificacion;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeColecciones;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeCriteriosDePertenencia;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeFuentes;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeUsuarios;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioDeHechos;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitud;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitudes;
import ar.edu.utn.frba.dds.server.templates.JavalinHandlebars;
import ar.edu.utn.frba.dds.server.templates.JavalinRenderer;
import ar.edu.utn.frba.dds.server.templates.TemplatingUtils;
import ar.edu.utn.frba.dds.servicio.HechosService;
import ar.edu.utn.frba.dds.servicio.SolicitudesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JavalinJackson;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

  public static final String PATH_GEO_JSON = "geojson/provincias.geojson";
  private static final Logger logger = LoggerFactory.getLogger(Server.class);

  private static void initializeStaticFiles(JavalinConfig config) {
    config.staticFiles.add(staticFileConfig -> {
      staticFileConfig.hostedPath = "/static";
      staticFileConfig.directory = "/static";
      staticFileConfig.location = Location.CLASSPATH;
    });

    config.staticFiles.add(staticFileConfig -> {
      staticFileConfig.hostedPath = "/";
      staticFileConfig.directory = "/static/img";
      staticFileConfig.location = Location.CLASSPATH;
    });

    config.staticFiles.add(staticFileConfig -> {
      staticFileConfig.hostedPath = "/uploads";
      staticFileConfig.directory = "uploads";
      staticFileConfig.location = Location.EXTERNAL;
    });
  }

  private static ProvinciasController crearProvinciasController(BuscadorDeProvincias buscador) {
    return new ProvinciasController(buscador);
  }

  private static void initializeJsonMapper(JavalinConfig config) {
    ObjectMapper customMapper = TemplatingUtils.objectMapper;
    config.jsonMapper(new JavalinJackson(customMapper, false));
  }

  private void manejoDeExcepciones(Javalin app) {
    app.exception(RuntimeException.class, (e, ctx) -> {

      logger.error("Se produjo un error no controlado:", e);

      var modelo = TemplatingUtils.errorMessage(
          e.getMessage() != null ? e.getMessage() : "Error inesperado",
          500
      );

      ctx.render("error.hbs", modelo);
    });
  }

  private void initializeTemplating(JavalinConfig config) {
    config.fileRenderer(
        new JavalinRenderer().register("hbs", new JavalinHandlebars())
    );
  }

  private HechosController crearHechosController(
          RepositorioDeUsuarios repositorioDeUsuarios,
          RepositorioDeHechos repositorioDeHechos,
          RepositorioDeFuentes repositorioDeFuentes) {

    String almacenamiento = System.getProperty("almacenamiento.archivos");
    AlmacenamientoDeArchivos almacenamientoDeArchivos;

    if ("CloudFare".equals(almacenamiento)) {
      almacenamientoDeArchivos = new AlmacenamientoEnCloudFare("tpa-ddsi-multimedia","/multimedia");
    } else if ("google".equals(almacenamiento)) {
      almacenamientoDeArchivos = new AlmacenamientoEnGoogleCloud("soporte-hechos", "multimedia");
    } else {
      almacenamientoDeArchivos = new AlmacenamientoLocal("/uploads");
    }

    HechosService hechosService = new HechosService(repositorioDeFuentes,
            repositorioDeHechos, almacenamientoDeArchivos, repositorioDeUsuarios
    );

    return new HechosController(hechosService);
  }


  @SuppressWarnings("checkstyle:WhitespaceAfter")
  public void start() {
    var app = Javalin.create(config -> {
      initializeStaticFiles(config);
      initializeTemplating(config);
      initializeJsonMapper(config);
      config.http.maxRequestSize = 26_000_000L; // 26 MB
    });

    manejoDeExcepciones(app);

    RepositorioDeHechos repositorioDeHechos = new RepositorioDeHechos();

    BuscadorDeProvincias buscadorDeProvincias = new BuscadorDeProvincias(PATH_GEO_JSON);

    new Bootstrap().init(buscadorDeProvincias, repositorioDeHechos);

    //ESTADISTICAS
    var repositorioEstadisticas = new RepositorioEstadisticas();
    var servicioDeEstadisticas = new EstadisticasServiceImpl(repositorioEstadisticas);

    var queryCategoriaConMasHechos = new CategoriaConMasHechos();
    var queryHoraConMasHechosPorCategoria = new HoraConMasHechosPorCategoria();
    var queryProvinciaConMasHechos = new ProvinciaConMasHechos();
    var queryProvinciaConMasHechosPorCategoria = new ProvinciaConMasHechosPorCategoria();

    servicioDeEstadisticas.addQuery(queryCategoriaConMasHechos);
    servicioDeEstadisticas.addQuery(queryHoraConMasHechosPorCategoria);
    servicioDeEstadisticas.addQuery(queryProvinciaConMasHechos);
    servicioDeEstadisticas.addQuery(queryProvinciaConMasHechosPorCategoria);
    var csvMaker = new ExportadorCsv(servicioDeEstadisticas);

    RepositorioDeColecciones repositorioDeColecciones = new RepositorioDeColecciones();

    RepositorioSolicitud repositorioSolicitud = new RepositorioSolicitudes();
    ProcesadorDeSolicitudesDeModificacion procesadorDeSolicitudesDeModificacion =
            new ProcesadorDeSolicitudesDeModificacion(repositorioSolicitud, repositorioDeHechos);
    List<SolicitudBajaObserver> solicitudBajaObservers =
            List.of(new DetectorSpamObserver(new DetectorDeSpamBasico()));
    ProcesadorDeSolicitudesDeBaja procesadorDeSolicitudesDeBaja =
            new ProcesadorDeSolicitudesDeBaja(repositorioSolicitud, solicitudBajaObservers);

    RepositorioDeUsuarios repositorioDeUsuarios = new RepositorioDeUsuarios();

    SessionController sessionController = new SessionController(repositorioDeUsuarios,
            repositorioDeColecciones, repositorioSolicitud, servicioDeEstadisticas, csvMaker);
    HealthCheckController healthCheckController = new HealthCheckController();

    // Fuentes
    RepositorioDeFuentes repositorioDeFuentes = new RepositorioDeFuentes();
    FuentesController fuentesController =
        new FuentesController(repositorioDeFuentes, repositorioDeHechos);

    // Colecciones
    RepositorioDeCriteriosDePertenencia repositorioDeCriteriosDePertenencia =
        new RepositorioDeCriteriosDePertenencia();
    ColeccionesController coleccionesController =
        new ColeccionesController(repositorioDeColecciones,
            repositorioDeFuentes, repositorioDeCriteriosDePertenencia);

    HechosController hechosController =
        crearHechosController(repositorioDeUsuarios, repositorioDeHechos, repositorioDeFuentes);
    ProvinciasController provinciasController = crearProvinciasController(buscadorDeProvincias);
    SolicitudesService solicitudesService = new SolicitudesService(
        repositorioSolicitud,
        repositorioDeUsuarios,
        procesadorDeSolicitudesDeBaja,
        procesadorDeSolicitudesDeModificacion
    );
    SolicitudesController solicitudesController = new SolicitudesController(solicitudesService);

    new Router().configure(
        app,
        hechosController,
        provinciasController,
        solicitudesController,
        healthCheckController,
        sessionController,
        fuentesController,
        coleccionesController
    );
    app.start(9001);
  }
}
