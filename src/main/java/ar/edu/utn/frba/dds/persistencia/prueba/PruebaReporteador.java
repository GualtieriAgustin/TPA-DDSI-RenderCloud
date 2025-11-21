package ar.edu.utn.frba.dds.persistencia.prueba;

import ar.edu.utn.frba.dds.dominio.estadisticas.EstadisticasServiceImpl;
import ar.edu.utn.frba.dds.dominio.estadisticas.ExportadorCsv;
import ar.edu.utn.frba.dds.dominio.estadisticas.RepositorioEstadisticas;
import ar.edu.utn.frba.dds.dominio.estadisticas.querys.CategoriaConMasHechos;
import ar.edu.utn.frba.dds.dominio.estadisticas.querys.HoraConMasHechosPorCategoria;
import ar.edu.utn.frba.dds.dominio.estadisticas.querys.ProvinciaConMasHechos;
import ar.edu.utn.frba.dds.dominio.estadisticas.querys.ProvinciaConMasHechosPorCategoria;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.Multimedia;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.TipoMultimedia;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeUsuarios;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioDeHechos;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import java.time.LocalDateTime;
import java.util.List;

public class PruebaReporteador implements SimplePersistenceTest {
  public static void main(String[] args) {
    new PruebaReporteador().test();
  }

  public void test() {
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

    var repositorioDeUsuarios = new RepositorioDeUsuarios();
    var repositorioDeHechos = new RepositorioDeHechos();

    withTransaction(() -> {
      var usuario = new Usuario("pepito");

      repositorioDeUsuarios.registrar(usuario);

      var multimedia = List.of(
          new Multimedia("https://tinyurl.com/mryxp858", TipoMultimedia.IMAGEN, "foto del suceso"
          )
      );

      var hecho1 = new Hecho(
          "hecho test",
          "algo pasó",
          "accidente",
          new Ubicacion(1.0, 2.0),
          LocalDateTime.of(2001, 9, 11, 11, 44),
          LocalDateTime.now(),
          multimedia,
          usuario,
          Provincia.BUENOS_AIRES
      );

      repositorioDeHechos.crear(hecho1);

      var hecho2 = new Hecho(
          "hecho test",
          "algo pasó",
          "accidente",
          new Ubicacion(1.0, 2.0),
          LocalDateTime.of(2001, 9, 11, 11, 46),
          LocalDateTime.now(),
          multimedia,
          usuario,
          Provincia.BUENOS_AIRES
      );

      repositorioDeHechos.crear(hecho2);

      var hecho3 = new Hecho(
          "hecho test",
          "algo pasó",
          "ROBO",
          new Ubicacion(1.0, 2.0),
          LocalDateTime.of(2001, 9, 11, 11, 46),
          LocalDateTime.now(),
          multimedia,
          usuario,
          Provincia.CHACO
      );

      repositorioDeHechos.crear(hecho3);


      servicioDeEstadisticas.generarEstadisticas(
              LocalDateTime.of(2001, 9, 11, 11, 45),
              LocalDateTime.of(2001, 9, 11, 11, 46).plusDays(5)
      );
    });

    String csv = csvMaker.guardarReporteCsv();
    System.out.println(csv);
  }
}
