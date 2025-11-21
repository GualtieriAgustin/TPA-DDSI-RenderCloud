package ar.edu.utn.frba.dds.servicio;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroHecho;
import ar.edu.utn.frba.dds.dominio.fuentes.Origen;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.AlmacenamientoDeArchivos;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.Multimedia;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.TipoMultimedia;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeFuentes;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeUsuarios;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioDeHechos;
import io.github.flbulgarelli.jpa.extras.TransactionalOps;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.UploadedFile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HechosService implements WithSimplePersistenceUnit, TransactionalOps {
  private static final Logger logger = LoggerFactory.getLogger(HechosService.class);
  private final RepositorioDeFuentes repositorioDeFuentes;
  private final RepositorioDeHechos repositorioDeHechos;
  private final AlmacenamientoDeArchivos almacenamientoDeArchivos;
  private final RepositorioDeUsuarios repositorioDeUsuarios;

  public HechosService(
      RepositorioDeFuentes repositorioDeFuentes,
      RepositorioDeHechos repositorioDeHechos,
      AlmacenamientoDeArchivos almacenamientoDeArchivos,
      RepositorioDeUsuarios repositorioDeUsuarios) {
    this.repositorioDeHechos = repositorioDeHechos;
    this.almacenamientoDeArchivos = almacenamientoDeArchivos;
    this.repositorioDeUsuarios = repositorioDeUsuarios;
    this.repositorioDeFuentes = repositorioDeFuentes;
  }

  public Hecho buscarPorId(Long id) {
    return actualizarUrlMultimedia(repositorioDeHechos.consultarPorId(id));
  }

  public List<Hecho> buscar(List<FiltroHecho> filtros, int pagina, int cantidad) {
    long tiempoTotalComienzo = System.currentTimeMillis();
    logger.info("Buscando hechos con {} filtros, página {} y cantidad {}",
        filtros.size(), pagina, cantidad);

    List<List<Hecho>> hechosPorFuente = repositorioDeFuentes
        .buscarTodas()
        .stream()
        .map(fuente -> {
          long comienzo = System.currentTimeMillis();
          List<Hecho> hechosDeFuente =
              fuente.obtenerHechosPorCriterio(new CriterioDePertenencia(filtros));
          long fin = System.currentTimeMillis();

          // Log para medir el tiempo de respuesta de cada fuente
          logger.info("Fuente '{}' tardó {} ms en devolver {} hechos.",
              fuente.getClass().getSimpleName(), (fin - comienzo), hechosDeFuente.size());

          return hechosDeFuente.stream()
              .sorted(Comparator.comparing(Hecho::getFechaCarga).reversed())
              .toList();
        })
        .filter(list -> !list.isEmpty())
        .toList();

    List<Hecho> resultadoFinal = intercalarPaginadoYlimitado(hechosPorFuente, pagina, cantidad)
        .stream()
        .map(this::actualizarUrlMultimedia)
        .toList();

    long tiempoTotalFin = System.currentTimeMillis();
    logger.info("Búsqueda total completada en {} ms. Se encontraron {} hechos.",
        (tiempoTotalFin - tiempoTotalComienzo), resultadoFinal.size());
    return resultadoFinal;
  }

  public void crearHecho(
      String titulo, String descripcion, String categoria, Double latitud, Double longitud,
      LocalDateTime fechaSuceso, Provincia provincia, List<UploadedFile> archivosSubidos,
      List<String> descripcionesArchivos, String username) {

    List<Multimedia> multimedias = new ArrayList<>();
    if (!archivosSubidos.isEmpty()) {
      logger.info("Procesando archivos multimedia...");
      for (int i = 0; i < archivosSubidos.size(); i++) {
        UploadedFile archivo = archivosSubidos.get(i);
        String descripcionArchivo = (i < descripcionesArchivos.size())
            ? StringUtils.substring(descripcionesArchivos.get(i), 0, 130) : "";
        String path = almacenamientoDeArchivos.guardar(archivo.content(), archivo.filename());
        TipoMultimedia tipo = TipoMultimedia.fromContentType(archivo.contentType());
        multimedias.add(new Multimedia(path, tipo, descripcionArchivo));
      }
    }

    Hecho hecho = new Hecho(
        titulo, descripcion, categoria, new Ubicacion(latitud, longitud),
        fechaSuceso, Origen.ANONIMO, LocalDateTime.now(), multimedias, provincia
    );

    if (username != null && !username.isEmpty()) {
      logger.debug("Hecho subido por el usuario {}", username);
      Usuario usuario = repositorioDeUsuarios.buscarUsuario(username);
      if (usuario != null) {
        hecho.setUsuario(usuario);
        hecho.setOrigen(Origen.CONTRIBUYENTE);
      }
    }

    withTransaction(() -> repositorioDeHechos.crear(hecho));
    logger.info("Hecho creado: {}", hecho);
  }

  private List<Hecho> intercalarPaginadoYlimitado(List<List<Hecho>> listasDeHechos, int pagina,
                                                  int cantidadPorPagina) {
    List<Hecho> resultado = new ArrayList<>();
    if (listasDeHechos.isEmpty()) {
      return resultado;
    }

    int offset = (pagina - 1) * cantidadPorPagina;
    int limite = cantidadPorPagina;
    int elementosSaltados = 0;
    int indiceHecho = 0;
    boolean huboAdiciones;

    do {
      huboAdiciones = false;
      for (List<Hecho> lista : listasDeHechos) {
        if (indiceHecho < lista.size()) {
          if (elementosSaltados < offset) {
            elementosSaltados++;
          } else if (resultado.size() < limite) {
            resultado.add(lista.get(indiceHecho));
          }
          huboAdiciones = true;
        }
      }
      indiceHecho++;
    } while (huboAdiciones && resultado.size() < limite);

    return resultado;
  }

  private Hecho actualizarUrlMultimedia(Hecho hecho) {
    if (hecho != null) {
      hecho.getMultimedias().forEach(
          multimedia ->
              multimedia.setUrl(almacenamientoDeArchivos.getUrlPublica(multimedia.getNombre()))
      );
    }
    return hecho;
  }
}