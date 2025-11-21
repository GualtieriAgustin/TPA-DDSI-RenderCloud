package ar.edu.utn.frba.dds.persistencia.prueba;

import ar.edu.utn.frba.dds.dominio.colecciones.Coleccion;
import ar.edu.utn.frba.dds.dominio.colecciones.ModoDeNavegacion;
import ar.edu.utn.frba.dds.dominio.colecciones.NavegadorDeColecciones;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.fuentes.Agregador;
import ar.edu.utn.frba.dds.dominio.fuentes.Fuente;
import ar.edu.utn.frba.dds.dominio.fuentes.FuenteEstatica;
import ar.edu.utn.frba.dds.dominio.fuentes.proxy.demo.FuenteDemo;
import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.FuenteMetaMapa;
import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.ServicioMetamapa;
import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.ServicioMetamapaRetrofitClient;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeColecciones;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitud;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitudes;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;

public class PruebaFuentes implements WithSimplePersistenceUnit {
  public static void main(String[] args) {
    new PruebaFuentes().test();
  }

  public void test() {
    Fuente fuenteDemo = new FuenteDemo("http://localhost:3000/fuenteDemo");
    Fuente fuenteEstatica = new FuenteEstatica(
        "/Users/alan.zhao/Documents/DDS/TP_Anual/tpa-2025-24/"
        + "src/main/resources/csv/accidentes_transito_fatales_min.csv");
    ServicioMetamapa instanciaMetamapa = new ServicioMetamapaRetrofitClient(
        "http://localhost:3000/fuenteMetamapa/");
    Fuente multifuenteMetamapa = new FuenteMetaMapa(
        List.of(instanciaMetamapa)
    );
    Fuente fuenteAgregador = new Agregador(
        List.of(fuenteDemo, fuenteEstatica, multifuenteMetamapa));

    Coleccion miColeccion = new Coleccion(
        "Hechos de Múltiples Metamapas",
        "...",
        fuenteAgregador,
        new CriterioDePertenencia(),
        ModoDeNavegacion.IRRESTRICTO
    );

    RepositorioDeColecciones repoColecciones = new RepositorioDeColecciones();

    withTransaction(() -> {
      repoColecciones.crear(miColeccion);
    });

    Coleccion coleccionDeDb = repoColecciones.consultarPorId(miColeccion.getId());

    RepositorioSolicitud repoSolicitudes = new RepositorioSolicitudes();

    System.out.println(coleccionDeDb);
    NavegadorDeColecciones navegador = new NavegadorDeColecciones(repoSolicitudes);

    List<Hecho> hechos = navegador.navegar(coleccionDeDb);
    System.out.println(hechos);
  }
}
