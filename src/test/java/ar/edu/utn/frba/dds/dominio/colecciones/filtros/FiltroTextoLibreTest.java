package ar.edu.utn.frba.dds.dominio.colecciones.filtros;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FiltroTextoLibreTest {

  private Hecho hechoDePrueba;

  @BeforeEach
  void setUp() {
    hechoDePrueba = new Hecho(
        "Accidente en la Autopista",
        "Un choque múltiple ocurrió en la Panamericana.",
        "Tránsito",
        new Ubicacion(-34.6037, -58.3816),
        LocalDateTime.now(),
        null,
        LocalDateTime.now(),
        Provincia.BUENOS_AIRES
    );
  }

  @Test
  void cumple_siElTextoEstaEnElTitulo_retornaTrue() {
    FiltroTextoLibre filtro = new FiltroTextoLibre("autopista");
    assertTrue(filtro.cumple(hechoDePrueba));
  }

  @Test
  void cumple_siElTextoEstaEnLaDescripcion_retornaTrue() {
    FiltroTextoLibre filtro = new FiltroTextoLibre("panamericana");
    assertTrue(filtro.cumple(hechoDePrueba));
  }

  @Test
  void cumple_siElTextoEstaEnLaCategoria_retornaTrue() {
    FiltroTextoLibre filtro = new FiltroTextoLibre("tránsito");
    assertTrue(filtro.cumple(hechoDePrueba));
  }

  @Test
  void cumple_ignoraMayusculasYMinusculas() {
    FiltroTextoLibre filtro = new FiltroTextoLibre("CHOQUE MÚLTIPLE");
    assertTrue(filtro.cumple(hechoDePrueba));
  }

  @Test
  void cumple_siElTextoNoSeEncuentra_retornaFalse() {
    FiltroTextoLibre filtro = new FiltroTextoLibre("inundación");
    assertFalse(filtro.cumple(hechoDePrueba));
  }

  @Test
  void cumple_ignoraEspaciosEnBlancoAlPrincipioYAlFinal() {
    FiltroTextoLibre filtro = new FiltroTextoLibre("  accidente  ");
    assertTrue(filtro.cumple(hechoDePrueba));
  }

  @Test
  void cumple_siElTextoEsUnaSubcadena_retornaTrue() {
    FiltroTextoLibre filtro = new FiltroTextoLibre("auto");
    assertTrue(filtro.cumple(hechoDePrueba));
  }
}
