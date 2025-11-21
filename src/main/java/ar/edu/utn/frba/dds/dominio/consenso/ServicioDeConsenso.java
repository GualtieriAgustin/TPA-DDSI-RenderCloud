package ar.edu.utn.frba.dds.dominio.consenso;


import ar.edu.utn.frba.dds.dominio.colecciones.Coleccion;
import ar.edu.utn.frba.dds.dominio.colecciones.NavegadorDeColecciones;
import ar.edu.utn.frba.dds.dominio.fuentes.Fuente;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Orquesta el cálculo de consenso para todas las colecciones.
 * Separa el cálculo costoso de datos globales de la aplicación rápida de las
 * estrategias de consenso, permitiendo recálculos eficientes.
 */
public class ServicioDeConsenso {

  //private final RepositorioSolicitud repositorioSolicitudes;
  private final NavegadorDeColecciones navegador;


  // Caché con los datos globales.
  private Map<Hecho, DatosGlobalesDeConsenso> datosGlobalesCacheados;

  public ServicioDeConsenso(NavegadorDeColecciones navegador) {
    //this.repositorioSolicitudes = repositorioSolicitudes;
    this.navegador = navegador;
  }

  /**
   * Recalcula los datos globales desde cero y luego actualiza todas las colecciones.
   */
  public void ponderarConsensoGlobal(
      List<Fuente> fuentesActuales, List<Coleccion> coleccionesActuales) {
    //this.datosGlobalesCacheados = this.calcularTodosLosDatosGlobales(fuentesActuales);

    List<Hecho> todosLosHechosActivos = navegador.obtenerTodosLosHechosActivos(fuentesActuales);
    this.datosGlobalesCacheados = this.calcularTodosLosDatosGlobales(
        todosLosHechosActivos, fuentesActuales.size());


    for (Coleccion coleccion : coleccionesActuales) {
      this.recalcularConsensoPara(coleccion);
    }
  }

  /**
   * Proceso RÁPIDO que se ejecuta bajo demanda (ej. cuando un usuario cambia la config).
   * Usa los datos globales ya cacheados para recalcular el consenso de una sola colección.
   *
   * @param coleccion La colección para la cual se recalculará el consenso.
   */
  public void recalcularConsensoPara(Coleccion coleccion) {
    // Si es la primera vez, no podemos ejecutar el proceso global porque no tenemos las listas.
    // La responsabilidad de llamar a ponderarConsensoGlobal primero es de la Aplicacion.
    if (datosGlobalesCacheados == null) {
      throw new IllegalStateException("Se debe llamar a ponderarConsensoGlobal()"
          + " antes de recalcular para una colección individual.");
    }

    // Cada hecho tiene un nivel de consenso para la estrategia configurada
    Map<Hecho, NivelDeConsenso> resultadosParaColeccion = new HashMap<>();

    // Obtenemos la estrategia a partir del enum de la colección.
    EstrategiaDeConsenso estrategia;
    if (coleccion.getAlgoritmoDeConsenso() != null) {
      estrategia = coleccion.getAlgoritmoDeConsenso().getEstrategia();
    } else {
      estrategia = (hecho, datos) -> NivelDeConsenso.CONSENSUADO;
    }

    // Iteramos sobre los hechos crudos de la fuente de la colección
    for (Hecho hecho : navegador.obtenerHechosDeColeccion(coleccion)) {
      DatosGlobalesDeConsenso datos = datosGlobalesCacheados.get(hecho);
      if (datos != null) {
        NivelDeConsenso nivel = estrategia.calcular(hecho, datos);
        resultadosParaColeccion.put(hecho, nivel);
      }
    }
    // Actualizamos la colección con los nuevos resultados
    coleccion.actualizarConsenso(resultadosParaColeccion);
  }

  /**
   * Lógica de cálculo de datos globales. Es la parte más costosa y se optimiza aquí
   * recorriendo todos los hechos una sola vez para construir los índices necesarios.
   *
   * @return Un mapa con los datos globales pre-calculados para cada hecho único.
   */
  private Map<Hecho, DatosGlobalesDeConsenso> calcularTodosLosDatosGlobales(
      List<Hecho> todosLosHechosActivos, int totalDeFuentes) {
    // Map<Hecho, cantDeMenciones>
    Map<Hecho, Long> mencionesPorHecho = new HashMap<>();
    // Map<Titulo, Set<Hecho>>, Set en vez de List porque son Hechos diferentes
    Map<String, Set<Hecho>> hechosPorTitulo = new HashMap<>();

    // Recorremos todos los hechos de todas las fuentes UNA SOLA VEZ para poblar los índices
    todosLosHechosActivos.forEach(hecho -> {
      mencionesPorHecho.merge(hecho, 1L, Long::sum);
      hechosPorTitulo.computeIfAbsent(hecho.getTitulo(), k -> new HashSet<>()).add(hecho);
    });

    Map<Hecho, DatosGlobalesDeConsenso> datosCalculados = new HashMap<>();

    // Ahora, construimos el objeto de datos globales para cada hecho único
    mencionesPorHecho.keySet().forEach(hecho -> {
      long menciones = mencionesPorHecho.get(hecho);
      boolean conflicto = hechosPorTitulo.get(hecho.getTitulo()).size() > 1;
      datosCalculados.put(hecho, new DatosGlobalesDeConsenso(totalDeFuentes, menciones, conflicto));
    });

    return datosCalculados;
  }
}