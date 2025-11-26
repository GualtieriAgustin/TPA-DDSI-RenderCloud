package ar.edu.utn.frba.dds.controlador;

import ar.edu.utn.frba.dds.dominio.fuentes.Fuente;
import ar.edu.utn.frba.dds.dominio.fuentes.FuenteDinamica;
import ar.edu.utn.frba.dds.dominio.fuentes.FuenteEstatica;
import ar.edu.utn.frba.dds.dominio.fuentes.cache.FuenteCacheable;
import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.FuenteMetaMapa;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeFuentes;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioDeHechos;
import io.github.flbulgarelli.jpa.extras.TransactionalOps;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class FuentesController implements WithSimplePersistenceUnit, TransactionalOps {
  private final RepositorioDeFuentes repositorioDeFuentes;
  private final RepositorioDeHechos repositorioDeHechos;

  public FuentesController(
      RepositorioDeFuentes repositorioDeFuentes,
      RepositorioDeHechos repositorioDeHechos
  ) {
    this.repositorioDeFuentes = repositorioDeFuentes;
    this.repositorioDeHechos = repositorioDeHechos;
  }

  public void showFuentes(@NotNull Context context) {
    Map<String, Object> modelo = new HashMap<>();
    List<FuenteEstatica> fuentesEstaticas =
        repositorioDeFuentes.buscarPorTipo(FuenteEstatica.class);
    List<FuenteDinamica> fuentesDinamicas =
        repositorioDeFuentes.buscarPorTipo(FuenteDinamica.class);
    // Inyectar el repositorio en cada fuente dinámica para que puedan cargar sus hechos
    fuentesDinamicas.forEach(fuente -> fuente.setRepositorioHechos(repositorioDeHechos));
    List<FuenteMetaMapa> fuentesProxy = repositorioDeFuentes.buscarPorTipo(FuenteMetaMapa.class);

    modelo.put("fuentesEstaticas", fuentesEstaticas);
    modelo.put("fuentesDinamicas", fuentesDinamicas);
    modelo.put("fuentesProxy", fuentesProxy);

    context.render("fuentes-panel.hbs", modelo);
  }

  public void refreshCaches(){
    for(FuenteCacheable fuente : repositorioDeFuentes.buscarPorTipo(FuenteCacheable.class)){
      fuente.refrescarCache();
    }
  }
}
