package ar.edu.utn.frba.dds.server;

import ar.edu.utn.frba.dds.dominio.archivos.LectorArchivoCsv;
import ar.edu.utn.frba.dds.dominio.fuentes.FuenteDinamica;
import ar.edu.utn.frba.dds.dominio.fuentes.FuenteEstatica;
import ar.edu.utn.frba.dds.dominio.fuentes.Origen;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import ar.edu.utn.frba.dds.dominio.hechos.geo.BuscadorDeProvincias;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.Multimedia;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.TipoMultimedia;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioDeHechos;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class Bootstrap implements WithSimplePersistenceUnit {

  public static final String PATH_DATA_SET_PRUEBA = "csv/accidentes_transito_unicos.csv";
  public static final String PATH_DATA_SET_NATURALES = "csv/desastres_naturales.csv";
  public static final String PATH_DATA_SET_CDD = "csv/centros_clandestinos_procesado.csv";
  public static final String PATH_SCRIPT_INICIAL = "scripts/db/create_fts_index.sql";

  public void init(BuscadorDeProvincias buscadorDeProvincias,
                   RepositorioDeHechos repositorioDeHechos) {

    LectorArchivoCsv lectorCsv = new LectorArchivoCsv(
        PATH_DATA_SET_PRUEBA,
        buscadorDeProvincias,
        LocalDateTime.now()
    );

    LectorArchivoCsv naturalesCsv = new LectorArchivoCsv(
        PATH_DATA_SET_NATURALES,
        buscadorDeProvincias,
        LocalDateTime.now()
    );

    LectorArchivoCsv ccdCsv = new LectorArchivoCsv(
        PATH_DATA_SET_CDD,
        buscadorDeProvincias,
        LocalDateTime.now()
    );

    Usuario usuarioDePrueba = crearUsuarioDePrueba();

    withTransaction(() -> {
      persist(new FuenteEstatica(lectorCsv));
      persist(new FuenteEstatica(naturalesCsv));
      persist(new FuenteEstatica(ccdCsv));
      persist(new FuenteDinamica(repositorioDeHechos));
      persist(usuarioDePrueba);
      persist(crearHechoDePrueba());
      persist(crearOtroHechoDePrueba());
      entityManager().createNativeQuery(leerScriptSql()).executeUpdate();
    });
  }

  private Hecho crearOtroHechoDePrueba() {
    return new Hecho(
        "Incendio en edificio en Palermo",
        "Los bomberos trabajaron durante horas para controlar el fuego en el histórico edificio.",
        "Incendio",
        new Ubicacion(-34.5862845, -58.4238586),
        LocalDateTime.now(),
        Origen.MANUAL,
        LocalDateTime.now(),
        Provincia.CABA
    );
  }

  private Usuario crearUsuarioDePrueba() {
    Usuario usuario = new Usuario(
        "Pepe",
        "$argon2i$v=19$m=65536,t=2,p=1$ZJzewIl"
                + "+9c2tWhsPlQztPw$0Ujq6I9vPsGc2thFt0+JrCF9ySuN7cD+OWaxUymPmao",
        "pepe@utn.com",
        "pepe"
    );
    usuario.setIsAdmin(true);
    return usuario;
  }

  private Hecho crearHechoDePrueba() {
    return new Hecho(
        "Argentina campeón del mundo",
        "Argentina gana su primera Copa del Mundo al vencer 3-1 a "
            + "los Países Bajos en la final disputada"
            + " en el Estadio Monumental de Buenos Aires. "
            + "El encuentro, jugado el 25 de junio de 1978, marcó un momento histórico "
            + "para el fútbol argentino y para todo el país. "
            + "Bajo la dirección técnica de César Luis Menotti,"
            + " la Selección mostró un estilo ofensivo, "
            + "basado en la posesión y el talento individual. "
            + "Mario Kempes, figura del torneo, abrió el marcador en la primera mitad "
            + "y volvió a anotar en el tiempo extra,"
            + " consolidando su papel como goleador del campeonato. "
            + "El ambiente fue electrizante: las tribunas colmadas,"
            + " los papeles celestes"
            + " y blancos cubriendo el cielo porteño, "
            + "y una nación entera unida detrás de su equipo. "
            + "Este título no solo representó un logro deportivo,"
            + " sino también un símbolo de identidad "
            + " y esperanza en un contexto social complejo. "
            + "A partir de ese día, el fútbol argentino se "
            + "consagró definitivamente en la élite mundial.",
        "Deportes",
        new Ubicacion(-34.5453, -58.4498),
        LocalDateTime.of(1978, 6, 25, 17, 0),
        Origen.MANUAL,
        LocalDateTime.now(),
        List.of(
            new Multimedia("multimedia/arg.png", TipoMultimedia.IMAGEN,
                "Daniel Passarella levanta la copa"),
            new Multimedia("multimedia/arg-2.png", TipoMultimedia.IMAGEN,
                "¡Argentina Campeón!"),
            new Multimedia("multimedia/arg-3.png", TipoMultimedia.IMAGEN,
                "Titulares Argentina '78"),
            new Multimedia("multimedia/arg-video.mp4", TipoMultimedia.VIDEO,
                "Nada que ver")
        ),
        Provincia.CABA
    );
  }

  private String leerScriptSql() {
    try (InputStream inputStream = getClass()
        .getClassLoader()
        .getResourceAsStream(Bootstrap.PATH_SCRIPT_INICIAL)) {
      if (inputStream == null) {
        throw new IOException(
            "No se pudo encontrar el archivo de script: " + Bootstrap.PATH_SCRIPT_INICIAL
        );
      }
      return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(
          "Error al leer el script SQL: " + Bootstrap.PATH_SCRIPT_INICIAL, e
      );
    }
  }
}
