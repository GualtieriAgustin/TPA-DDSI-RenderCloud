package ar.edu.utn.frba.dds.controlador;

import ar.edu.utn.frba.dds.dominio.colecciones.Coleccion;
import ar.edu.utn.frba.dds.dominio.colecciones.ModoDeNavegacion;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.consenso.AlgoritmoDeConsenso;
import ar.edu.utn.frba.dds.dominio.fuentes.Fuente;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeColecciones;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeCriteriosDePertenencia;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeFuentes;
import io.github.flbulgarelli.jpa.extras.TransactionalOps;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class ColeccionesController implements WithSimplePersistenceUnit, TransactionalOps {

  private final RepositorioDeColecciones repositorioDeColecciones;
  private final RepositorioDeFuentes repositorioDeFuentes;
  private final RepositorioDeCriteriosDePertenencia repositorioDeCriteriosDePertenencia;

  public ColeccionesController(
      RepositorioDeColecciones repositorioDeColecciones,
      RepositorioDeFuentes repositorioDeFuentes,
      RepositorioDeCriteriosDePertenencia repositorioDeCriteriosDePertenencia
  ) {
    this.repositorioDeColecciones = repositorioDeColecciones;
    this.repositorioDeFuentes = repositorioDeFuentes;
    this.repositorioDeCriteriosDePertenencia = repositorioDeCriteriosDePertenencia;
  }

  public void showColecciones(@NotNull Context context) {
    Map<String, Object> modelo = new HashMap<>();
    List<Coleccion> colecciones = repositorioDeColecciones.consultarTodas();

    modelo.put("colecciones", colecciones);

    context.render("colecciones-panel.hbs", modelo);
  }

  public void mostrarFormulario(@NotNull Context context) {
    Map<String, Object> modelo = new HashMap<>();
    List<Fuente> fuentes = repositorioDeFuentes.buscarTodas();
    List<CriterioDePertenencia> criterios = repositorioDeCriteriosDePertenencia.consultarTodos();

    modelo.put("fuentes", fuentes);
    modelo.put("criterios", criterios);
    context.render("subir-coleccion.hbs", modelo);
  }

  public void subirColeccion(Context ctx) {
    try {
      String titulo = Objects.requireNonNull(ctx.formParam("titulo"));
      String descripcion = ctx.formParam("descripcion");
      Long fuenteId = ctx.formParamAsClass("fuenteId", Long.class).get();
      Long criterioId = ctx.formParamAsClass("criterioId", Long.class).get();
      ModoDeNavegacion modoNavegacion = ModoDeNavegacion.valueOf(
          Objects.requireNonNull(ctx.formParam("modo")));
      AlgoritmoDeConsenso algoritmo = AlgoritmoDeConsenso.valueOf(
          Objects.requireNonNull(ctx.formParam("algoritmo")));

      Fuente fuente = repositorioDeFuentes.buscarPorId(fuenteId).orElseThrow(
          () -> new RuntimeException("La fuente con ID " + fuenteId + " no fue encontrada."));
      CriterioDePertenencia criterio = repositorioDeCriteriosDePertenencia.buscarPorId(criterioId)
          .orElseThrow(() -> new RuntimeException(
              "El criterio con ID " + criterioId + " no fue encontrado."));

      Coleccion nuevaColeccion = new Coleccion(
          titulo,
          descripcion,
          fuente,
          criterio,
          modoNavegacion
      );
      nuevaColeccion.setAlgoritmoDeConsenso(algoritmo);

      withTransaction(() -> {
        repositorioDeColecciones.crear(nuevaColeccion);
      });

      ctx.redirect("/user/admin/colecciones");

    } catch (RuntimeException e) {
      ctx.status(400).render("subir-coleccion.hbs",
          Map.of("error", "Error al crear la colección: " + e.getMessage()));
    }
  }
}
