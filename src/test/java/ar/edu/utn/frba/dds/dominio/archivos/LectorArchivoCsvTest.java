package ar.edu.utn.frba.dds.dominio.archivos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.utn.frba.dds.dominio.fuentes.FuenteEstatica;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class LectorArchivoCsvTest {
  @Test
  void importarHechosDesdeArchivoCsvCorrectamente() {
    // Ruta del CSV
    var pathCsv = "accidentes_transito_fatales.csv";

    // Crea el lector e instancia la fuente
    LectorArchivo lector = new LectorArchivoCsv(pathCsv, null);
    FuenteEstatica fuente = new FuenteEstatica(lector);

    // Verificaion de que no este vacio
    assertFalse(fuente.getHechos().isEmpty(), "La fuente debería haber importado hechos.");

    // Buscar un hecho concreto por título
    Optional<Hecho> hechoOpt = fuente.getHechos().stream()
        .filter(h -> h.getTitulo().equals("13075-Accidente de tránsito con víctima fatal"))
        .findFirst();

    assertTrue(hechoOpt.isPresent(), "Debería existir el hecho con título esperado.");

    Hecho hecho = hechoOpt.get();

    // Verifica algunos campos específicos
    assertEquals("Accidente de tránsito", hecho.getCategoria());
    Ubicacion ubicacion = hecho.getUbicacion();
    assertEquals(-39.0, ubicacion.getLatitud());
    assertEquals(-68.0, ubicacion.getLongitud());
  }

  @Test
  public void getFileNameDevuelveNombreCorrectoDelArchivo() {
    var path = "accidentes_transito_fatales.csv";

    LectorArchivoCsv lector = new LectorArchivoCsv(path, null);

    assertEquals("accidentes_transito_fatales.csv", lector.getFileName());
  }

  @Test
  public void importarHechosDeOtraFuenteCsvCorrectamente() {
    var pathCsv = "desastres_naturales.csv";
    LectorArchivo lector = new LectorArchivoCsv(pathCsv, null);
    FuenteEstatica fuente = new FuenteEstatica(lector);

    assertFalse(fuente.getHechos().isEmpty(), "La fuente debería haber importado hechos.");

    Optional<Hecho> hechoOpt = fuente.getHechos().stream()
        .filter(h -> h.getTitulo().equals("1999-9388-SOM-Desastre natural"))
        .findFirst();

    assertTrue(hechoOpt.isPresent(), "Debería existir el hecho con título esperado.");

    Hecho hecho = hechoOpt.get();

    assertEquals("Desastre natural", hecho.getCategoria());
    assertEquals("Desastre natural de tipo climatológico: Sequía. Total de muertos: 21", hecho.getDescripcion());
    Ubicacion ubicacion = hecho.getUbicacion();
    assertEquals(10.0, ubicacion.getLatitud());
    assertEquals(49.0, ubicacion.getLongitud());
    assertEquals(LocalDateTime.parse("2000-01-01T00:00"), hecho.getFechaSuceso());
  }
}
