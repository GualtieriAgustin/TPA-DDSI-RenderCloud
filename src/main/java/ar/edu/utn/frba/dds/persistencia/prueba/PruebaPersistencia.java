package ar.edu.utn.frba.dds.persistencia.prueba;

import ar.edu.utn.frba.dds.dominio.colecciones.Coleccion;
import ar.edu.utn.frba.dds.dominio.colecciones.ModoDeNavegacion;
import ar.edu.utn.frba.dds.dominio.colecciones.NavegadorDeColecciones;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroCategoria;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroTitulo;
import ar.edu.utn.frba.dds.dominio.fuentes.FuenteDinamica;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.Multimedia;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.TipoMultimedia;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeColecciones;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeCriteriosDePertenencia;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeFiltrosHecho;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeUsuarios;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioDeHechos;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitudes;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import java.time.LocalDateTime;
import java.util.List;

public class PruebaPersistencia implements SimplePersistenceTest {
  public static void main(String[] args) {
    new PruebaPersistencia().test();
  }

  public void test() {
    var usuario = new Usuario("pepito");
    var repo = new RepositorioDeUsuarios();
    var repoHecho = new RepositorioDeHechos();
    var multimedia = List.of(
        new Multimedia("https://tinyurl.com/mryxp858", TipoMultimedia.IMAGEN, "foto del suceso"
        )
    );
    var hecho = new Hecho(
        "hecho test",
        "algo pasó",
        "accidente",
        new Ubicacion(1.0, 2.0),
        LocalDateTime.of(2001, 9, 11, 11, 46),
        LocalDateTime.now(),
        multimedia,
        usuario,
        Provincia.PROVINCIA_DESCONOCIDA
    );

    var hecho2 = new Hecho(
        "internaron al loro",
        "hecho que no deberia ser incluido por la coleccion",
        "concierto",
        new Ubicacion(1.0, 2.0),
        LocalDateTime.of(2024, 1, 13, 0, 0),
        LocalDateTime.now(),
        null,
        usuario,
        Provincia.CABA
    );

    var filtro = new FiltroCategoria("accidente");
    var criterio = new CriterioDePertenencia(filtro);
    var repoColecciones = new RepositorioDeColecciones();
    var repoFiltros = new RepositorioDeFiltrosHecho();
    var repoSolicitudes = new RepositorioSolicitudes();
    var repoCriterios = new RepositorioDeCriteriosDePertenencia();
    var fuenteDinamica = new FuenteDinamica(new RepositorioDeHechos());
    var coleccion = new Coleccion(
        "coleccion 1",
        "test",
        fuenteDinamica,
        criterio,
        ModoDeNavegacion.IRRESTRICTO
    );

    withTransaction(() -> {
      repo.registrar(usuario);
      repoHecho.crear(hecho);
      repoHecho.crear(hecho2);
      repoCriterios.crear(criterio);
      var hechoRecuperado = repoHecho.consultarPorId(1L);
      System.out.println("hecho de db: " + hechoRecuperado);
      var hechoPorCriterio = repoHecho.consultarPorCriterio(criterio);
      System.out.println("hecho de db por criterio: " + hechoPorCriterio);
      repoColecciones.crear(coleccion);

      var navegadorDeColecciones = new NavegadorDeColecciones(repoSolicitudes);
      var hechosNavegables = navegadorDeColecciones
          .navegar(coleccion, List.of(new FiltroTitulo("hecho test")));
      System.out.println("hechos navegables: " + hechosNavegables);
      System.out.println("filtros:" + repoFiltros.consultarTodos());
      System.out.println("criterios:" + repoCriterios.consultarTodos());
    });
  }
}
