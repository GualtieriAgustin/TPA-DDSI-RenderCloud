package ar.edu.utn.frba.dds.persistencia.prueba;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroTextoLibre;
import ar.edu.utn.frba.dds.dominio.fuentes.Origen;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioDeHechos;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import java.time.LocalDateTime;
import java.util.List;

public class PruebaFullTextSearch implements SimplePersistenceTest {

  public static void main(String[] args) {
    new PruebaFullTextSearch().test();
  }

  public void test() {
    var repoHechos = new RepositorioDeHechos();

    var hecho1 = new Hecho(
        "Gran incendio en un edificio de Palermo",
        "Los bomberos trabajaron durante horas para controlar el fuego en el histórico edificio.",
        "incendio",
        new Ubicacion(-34.58, -58.42),
        LocalDateTime.now(),
        Origen.PROXY,
        LocalDateTime.now(),
        Provincia.CABA);

    var hecho2 = new Hecho(
        "Demoras en la línea B del subte",
        "Un problema técnico en una formación causa demoras significativas.",
        "transporte",
        new Ubicacion(-34.60, -58.40),
        LocalDateTime.now(),
        Origen.PROXY,
        LocalDateTime.now(),
        Provincia.CABA);

    var hecho3 = new Hecho(
        "Se inauguró un nuevo edificio de oficinas",
        "El moderno edificio cuenta con tecnología de punta y vistas panorámicas.",
        "urbanismo",
        new Ubicacion(-34.59, -58.37),
        LocalDateTime.now(),
        Origen.PROXY,
        LocalDateTime.now(),
        Provincia.CABA);

    var hecho4 = new Hecho(
        "Robo en el banco",
        "Los ladrones se llevaron una gran cantidad de dinero en efectivo.",
        "crimen",
        new Ubicacion(-34.60, -58.38),
        LocalDateTime.now(),
        Origen.PROXY,
        LocalDateTime.now(),
        Provincia.CABA);

    withTransaction(() -> {
      repoHechos.crear(hecho1);
      repoHechos.crear(hecho2);
      repoHechos.crear(hecho3);
      repoHechos.crear(hecho4);
    });

    System.out.println("--- Hechos creados en la base de datos ---");

    // Caso 1: Búsqueda sensible a mayúsculas
    String textoBusqueda = "edificio";
    System.out.println(
        "\n--- Buscando hechos que contengan la palabra: '" + textoBusqueda + "' ---");
    var criterioFts = new CriterioDePertenencia(new FiltroTextoLibre(textoBusqueda));
    List<Hecho> resultados = repoHechos.consultarPorCriterio(criterioFts);
    System.out.println("Resultados encontrados: " + resultados.size());
    resultados.forEach(hecho -> {
      System.out.println("  - Título: " + hecho.getTitulo());
      System.out.println("    Descripción: " + hecho.getDescripcion());
    });

    // Caso 2: Búsqueda insensible a mayúsculas
    String textoMayusculas = "EDIFICIO";
    System.out.println("\n--- Buscando hechos que contengan la palabra: '"
        + textoMayusculas + "' (insensible a mayúsculas) ---");
    var criterioMayusculas = new CriterioDePertenencia(new FiltroTextoLibre(textoMayusculas));
    List<Hecho> resultadosMayusculas = repoHechos.consultarPorCriterio(criterioMayusculas);
    System.out.println("Resultados encontrados: " + resultadosMayusculas.size());
    resultadosMayusculas.forEach(hecho -> System.out.println("  - Título: " + hecho.getTitulo()));

    // Caso 3: Búsqueda por raíz
    String textoStemming = "bombero";
    System.out.println("\n--- Buscando 'bombero' (raíz de 'bomberos') ---");
    var criterioStemming = new CriterioDePertenencia(new FiltroTextoLibre(textoStemming));
    List<Hecho> resultadosStemming = repoHechos.consultarPorCriterio(criterioStemming);
    System.out.println("Resultados encontrados: " + resultadosStemming.size());
    resultadosStemming.forEach(hecho -> System.out.println("  - Título: " + hecho.getTitulo()));

    // Caso 4: Búsqueda con múltiples términos
    String textoMultiTermino = "fuego edificio";
    System.out.println(
        "\n--- Buscando '" + textoMultiTermino + "' (múltiples términos) ---");
    var criterioMultiTermino = new CriterioDePertenencia(new FiltroTextoLibre(textoMultiTermino));
    List<Hecho> resultadosMultiTermino = repoHechos.consultarPorCriterio(criterioMultiTermino);
    System.out.println("Resultados encontrados: " + resultadosMultiTermino.size());
    resultadosMultiTermino.forEach(
        hecho -> System.out.println(" ID: " + hecho.getId() + "  - Título: " + hecho.getTitulo()));

    // Caso 5: Búsqueda por similitud con error de tipeo
    var textoConError = "edifisio";
    System.out.println("\n--- Buscando: '" + textoConError + "' para encontrar 'edificio' ---");
    var criterioSimilitud = new CriterioDePertenencia(new FiltroTextoLibre(textoConError));
    List<Hecho> resultadosSimilitud = repoHechos.consultarPorCriterio(criterioSimilitud);
    System.out.println("Resultados encontrados: " + resultadosSimilitud.size());
    resultadosSimilitud.forEach(hecho -> System.out.println("  - Título: " + hecho.getTitulo()));

    // Caso 6: Búsqueda por similitud con error de tipeo
    var palabraIncompleta = "edi";
    System.out.println("\n--- Busco: '" + palabraIncompleta + "' para encontrar 'edificio' ---");
    var criterioParcial = new CriterioDePertenencia(new FiltroTextoLibre(textoConError));
    List<Hecho> resultadoParcial = repoHechos.consultarPorCriterio(criterioParcial);
    System.out.println("Resultados encontrados: " + resultadoParcial.size());
    resultadosSimilitud.forEach(hecho -> System.out.println("  - Título: " + hecho.getTitulo()));
  }
}