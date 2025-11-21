package ar.edu.utn.frba.dds.dominio.fuentes.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CacheDeHechosTest {

  CacheDeHechos cacheDeHechos;

  @BeforeEach
  public void setup() {
    cacheDeHechos = new CacheDeHechos();
  }

  @Test
  public void testGetHechos_CacheHit_RetornaDeCache() {
    CriterioDePertenencia criterio = mock(CriterioDePertenencia.class);
    List<Hecho> hechosCacheados = List.of(mock(Hecho.class));
    HechosConTiempo memoria = new HechosConTiempo(hechosCacheados);

    // Pongo en cache antes de llamar
    cacheDeHechos.hechosEnMemoria.put(criterio, memoria);

    // Obtengo de cache
    Optional<List<Hecho>> resultado = cacheDeHechos.getHechos(criterio);

    assertEquals(hechosCacheados, resultado.get());
  }

  @Test
  public void testGetHechos_CacheMiss_RetornaNulo() {
    CriterioDePertenencia criterio = mock(CriterioDePertenencia.class);

    Optional<List<Hecho>> resultado = cacheDeHechos.getHechos(criterio);

    assertTrue(resultado.isEmpty());
  }

  @Test
  public void testActualizarCache_ReemplazaCacheExistente() {
    CriterioDePertenencia criterio = mock(CriterioDePertenencia.class);
    List<Hecho> hechosIniciales = List.of(mock(Hecho.class));
    List<Hecho> hechosNuevos = List.of(mock(Hecho.class));

    cacheDeHechos.actualizarCache(criterio, hechosIniciales);
    assertEquals(hechosIniciales, cacheDeHechos.hechosEnMemoria.get(criterio).getHechos());

    cacheDeHechos.actualizarCache(criterio, hechosNuevos);
    assertEquals(hechosNuevos, cacheDeHechos.hechosEnMemoria.get(criterio).getHechos());
  }

  @Test
  public void testEliminarVencidos_EliminaCacheVencida() {
    CriterioDePertenencia criterio1 = mock(CriterioDePertenencia.class);
    CriterioDePertenencia criterio2 = mock(CriterioDePertenencia.class);

    HechosConTiempo memoriaVencida = mock(HechosConTiempo.class);
    when(memoriaVencida.getTimestamp()).thenReturn(LocalDateTime.now().minusHours(2));

    HechosConTiempo memoriaNoVencida = mock(HechosConTiempo.class);
    when(memoriaNoVencida.getTimestamp()).thenReturn(LocalDateTime.now());

    cacheDeHechos.hechosEnMemoria.put(criterio1, memoriaVencida);
    cacheDeHechos.hechosEnMemoria.put(criterio2, memoriaNoVencida);

    cacheDeHechos.eliminarVencidos();

    assertFalse(cacheDeHechos.hechosEnMemoria.containsKey(criterio1), "Debe eliminar el cache vencido");
    assertTrue(cacheDeHechos.hechosEnMemoria.containsKey(criterio2), "No debe eliminar el cache vigente");
  }
}
