package ar.edu.utn.frba.dds.dominio.colecciones;

import static java.util.Collections.emptyList;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroHecho;
import ar.edu.utn.frba.dds.dominio.fuentes.Fuente;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitud;
import java.util.List;

public class NavegadorDeColecciones {

  private final RepositorioSolicitud repoSolicitudes;

  public NavegadorDeColecciones(RepositorioSolicitud repoSolicitudes) {
    this.repoSolicitudes = repoSolicitudes;
  }

  public List<Hecho> obtenerTodosLosHechosActivos(List<Fuente> fuentes) {
    return fuentes.stream()
        .flatMap(fuente -> fuente.getHechos().stream())
        .filter(repoSolicitudes::estaActivo)
        .toList();
  }

  public List<Hecho> navegar(Coleccion coleccion, List<FiltroHecho> filtrosAdicionales) {
    return obtenerHechosDeColeccion(coleccion).stream()
        .filter(coleccion::cumpleModoDeNavegacion)
        .filter(hecho -> filtrosAdicionales.stream().allMatch(filtro -> filtro.cumple(hecho)))
        .toList();
  }

  public List<Hecho> navegar(Coleccion coleccion) {
    return this.navegar(coleccion, emptyList());
  }

  /**
   * Devuelve la lista de hechos base que pertenecen a la colección y están activos,
   * antes de aplicar filtros de navegación como el consenso.
   *
   * @param coleccion La colección de la cual obtener los hechos.
   * @return Una lista de hechos base de la colección.
   */
  public List<Hecho> obtenerHechosDeColeccion(Coleccion coleccion) {
    return coleccion.obtenerFuente().getHechos().stream()
        .filter(coleccion::pertenece)
        .filter(repoSolicitudes::estaActivo)
        .toList();
  }
}