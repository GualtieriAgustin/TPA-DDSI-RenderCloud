package ar.edu.utn.frba.dds.dominio.colecciones.filtros;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.fecha.rango.FiltroFechaRangoCarga;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.fecha.rango.FiltroFechaRangoSuceso;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class CriterioDePertenenciaTest {

  @Test
  public void dosCriteriosSonIgualesSiNoTienenFiltros() {
    CriterioDePertenencia criterio1 = new CriterioDePertenencia(Collections.emptyList());
    CriterioDePertenencia criterio2 = new CriterioDePertenencia(Collections.emptyList());

    assertEquals(criterio1, criterio2);
  }

  @Test
  public void dosCriteriosSonIgualesSiTienenLosMismosFiltros() {
    FiltroHecho filtro1 = new FiltroTitulo("titulo1");
    FiltroHecho filtro2 = new FiltroTitulo("titulo1");
    CriterioDePertenencia criterio1 = new CriterioDePertenencia(List.of(filtro1));
    CriterioDePertenencia criterio2 = new CriterioDePertenencia(List.of(filtro2));

    assertEquals(criterio1, criterio2);
  }

  @Test
  public void dosCriteriosSonIgualesSiTienenUnFiltroConIntervaloDeFechaIgual() {
    LocalDateTime fechaDesde = LocalDateTime.of(2025, 1, 1, 1, 1);
    LocalDateTime fechaHasta = LocalDateTime.of(2025, 1, 2, 1, 1);

    FiltroHecho filtro1 = new FiltroFechaRangoSuceso(fechaDesde, fechaHasta);
    FiltroHecho filtro2 = new FiltroFechaRangoSuceso(fechaDesde, fechaHasta);
    FiltroHecho filtro3 = new FiltroFechaRangoCarga(fechaDesde, fechaHasta);
    FiltroHecho filtro4 = new FiltroFechaRangoCarga(fechaDesde, fechaHasta);


    CriterioDePertenencia criterio1 = new CriterioDePertenencia(List.of(filtro1));
    CriterioDePertenencia criterio2 = new CriterioDePertenencia(List.of(filtro2));

    assertEquals(filtro1, filtro2);
    assertEquals(filtro3, filtro4);
    assertEquals(criterio1, criterio2);
  }

  @Test
  public void cumple_DebeValidarCorrectamenteLosFiltros() {
    // Arrange
    // El criterio requiere que el título contenga "Incendio" Y la categoría sea "Accidente".
    FiltroHecho filtroTitulo = new FiltroTitulo("Incendio");
    FiltroHecho filtroCategoria = new FiltroCategoria("Accidente");
    CriterioDePertenencia criterio = new CriterioDePertenencia(List.of(filtroTitulo, filtroCategoria));

    // Este hecho cumple ambos filtros.
    Hecho hechoQueCumple = new HechoTestBuilder()
        .conTitulo("Grave Incendio en Almagro")
        .conCategoria("Accidente")
        .build();

    // Este hecho solo cumple el filtro de categoría, pero no el de título.
    Hecho hechoQueNoCumple = new HechoTestBuilder()
        .conTitulo("Choque en la autopista")
        .conCategoria("Accidente")
        .build();

    // Act & Assert
    assertTrue(criterio.cumple(hechoQueCumple), "Debería cumplir porque matchea todos los filtros.");
    assertFalse(criterio.cumple(hechoQueNoCumple), "No debería cumplir porque el título no matchea.");
  }
}