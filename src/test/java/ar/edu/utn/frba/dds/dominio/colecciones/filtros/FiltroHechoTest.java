package ar.edu.utn.frba.dds.dominio.colecciones.filtros;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.fecha.exacta.FiltroFechaCarga;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.fecha.exacta.FiltroFechaSuceso;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.fecha.rango.FiltroFechaRangoCarga;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.fecha.rango.FiltroFechaRangoSuceso;
import ar.edu.utn.frba.dds.dominio.fuentes.Origen;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class FiltroHechoTest {

  @Test
  public void cumple_CuandoHechoCumpleConElFiltro_DebeRetornarTrue() {
    // Arrange
    List<FiltroHecho> filtros = List.of(
        new FiltroTitulo("Incendio"),
        new FiltroCategoria("Accidente"),
        new FiltroOrigen(Origen.DATASET)
    );

    Hecho hecho = new HechoTestBuilder()
        .conTitulo("Incendio en edificio")
        .conDescripcion("Un incendio ocurrió en un edificio")
        .conCategoria("Accidente")
        .conOrigen(Origen.DATASET)
        .build();

    // Act
    boolean resultado = filtros.stream().allMatch(filtro -> filtro.cumple(hecho));

    // Assert
    assertTrue(resultado);
  }

  @Test
  public void cumple_CuandoHechoNoCumpleConElFiltro_DebeRetornarFalse() {
    // Arrange
    List<FiltroHecho> filtros = List.of(
        new FiltroTitulo("Robo"),
        new FiltroCategoria("Delito")
    );

    Hecho hecho = new HechoTestBuilder()
        .conTitulo("Incendio en edificio")
        .conDescripcion("Un incendio ocurrió en un edificio")
        .conCategoria("Accidente")
        .build();

    // Act
    boolean resultado = filtros.stream().allMatch(filtro -> filtro.cumple(hecho));

    // Assert
    assertFalse(resultado);
  }

  @Test
  public void filtroDescripcion() {
    String unaDescripcion = "una descripcion";
    Hecho hecho = new HechoTestBuilder().conDescripcion(unaDescripcion).build();
    FiltroHecho filtro = new FiltroDescripcion(unaDescripcion);
    assertTrue(filtro.cumple(hecho));
  }

  @Test
  public void filtroDescripcionNoCoincide() {
    Hecho hecho = new HechoTestBuilder().conDescripcion("a").build();
    FiltroHecho filtro = new FiltroDescripcion("b");
    assertFalse(filtro.cumple(hecho));
  }

  @Test
  public void filtroFechaSuceso() {
    LocalDateTime fecha = LocalDateTime.now();
    Hecho hecho = new HechoTestBuilder().conFechaSuceso(fecha).build();
    FiltroHecho filtro = new FiltroFechaSuceso(fecha);
    assertTrue(filtro.cumple(hecho));
  }

  @Test
  public void filtroFechaSucesoNoCoincide() {
    Hecho hecho = new HechoTestBuilder().conFechaSuceso(LocalDateTime.of(1, 1, 1, 1, 1, 1)).build();
    FiltroHecho filtro = new FiltroFechaSuceso(LocalDateTime.of(2, 1, 1, 1, 1, 1));
    assertFalse(filtro.cumple(hecho));
  }

  @Test
  public void filtroProvinciaNoCoincide() {
    Hecho hecho = new HechoTestBuilder().conProvincia(Provincia.CABA).build();
    FiltroHecho filtro = new FiltroProvincia(Provincia.BUENOS_AIRES);
    assertFalse(filtro.cumple(hecho));
  }

  @Test
  public void filtroFechaCarga() {
    LocalDateTime fecha = LocalDateTime.now();
    Hecho hecho = new HechoTestBuilder().conFechaCarga(fecha).build();
    FiltroHecho filtro = new FiltroFechaCarga(fecha);
    assertTrue(filtro.cumple(hecho));
  }

  @Test
  public void filtroFechaCargaRango() {
    LocalDateTime desde = LocalDateTime.of(2025, 1, 1, 1, 1);
    LocalDateTime fecha = LocalDateTime.of(2025, 1, 2, 1, 1);
    LocalDateTime hasta = LocalDateTime.of(2025, 1, 3, 1, 1);
    Hecho hecho = new HechoTestBuilder().conFechaCarga(fecha).build();
    FiltroHecho filtro = new FiltroFechaRangoCarga(desde, hasta);
    assertTrue(filtro.cumple(hecho));
  }

  @Test
  public void filtroFechaCargaRangoNoCumplePorFueraDeRango() {
    LocalDateTime desde = LocalDateTime.of(2025, 1, 1, 1, 1);
    LocalDateTime hasta = LocalDateTime.of(2025, 1, 2, 1, 1);
    LocalDateTime fecha = LocalDateTime.of(2025, 1, 3, 1, 1);
    Hecho hecho = new HechoTestBuilder().conFechaCarga(fecha).build();
    FiltroHecho filtro = new FiltroFechaRangoCarga(desde, hasta);
    assertFalse(filtro.cumple(hecho));
  }

  @Test
  public void filtroFechaSucesoRango() {
    LocalDateTime desde = LocalDateTime.of(2025, 1, 1, 1, 1);
    LocalDateTime fecha = LocalDateTime.of(2025, 1, 2, 1, 1);
    LocalDateTime hasta = LocalDateTime.of(2025, 1, 3, 1, 1);
    Hecho hecho = new HechoTestBuilder().conFechaSuceso(fecha).build();
    FiltroHecho filtro = new FiltroFechaRangoSuceso(desde, hasta);
    assertTrue(filtro.cumple(hecho));
  }

  @Test
  public void filtroFechaCargaNoCoincide() {
    Hecho hecho = new HechoTestBuilder().conFechaCarga(LocalDateTime.of(1, 1, 1, 1, 1, 1)).build();
    FiltroHecho filtro = new FiltroFechaCarga(LocalDateTime.of(2, 1, 1, 1, 1, 1));
    assertFalse(filtro.cumple(hecho));
  }

  @Test
  public void filtroUbicacion() {
    Ubicacion ubicacion = new Ubicacion(0.0, 0.0);
    Hecho hecho = new HechoTestBuilder().conUbicacion(ubicacion).build();
    FiltroHecho filtro = new FiltroUbicacion(ubicacion);
    assertTrue(filtro.cumple(hecho));
  }

  @Test
  public void filtroUbicacionNoCoincide() {
    Ubicacion ubicacion1 = new Ubicacion(0.0, 0.0);
    Ubicacion ubicacion2 = new Ubicacion(1.0, 1.0);
    Hecho hecho = new HechoTestBuilder().conUbicacion(ubicacion1).build();
    FiltroHecho filtro = new FiltroUbicacion(ubicacion2);
    assertFalse(filtro.cumple(hecho));
  }

  @Test
  public void filtrosSonIguales() {
    FiltroTitulo filtroTitulo1 = new FiltroTitulo("t1");
    FiltroTitulo filtroTitulo2 = new FiltroTitulo("t1");

    FiltroDescripcion filtroDescripcion1 = new FiltroDescripcion("d1");
    FiltroDescripcion filtroDescripcion2 = new FiltroDescripcion("d1");

    FiltroCategoria filtroCategoria1 = new FiltroCategoria("cat1");
    FiltroCategoria filtroCategoria2 = new FiltroCategoria("cat1");

    assertEquals(filtroTitulo1, filtroTitulo2);
    assertEquals(filtroDescripcion1, filtroDescripcion2);
    assertEquals(filtroCategoria1, filtroCategoria2);
  }

  @Test
  public void filtroOrigenCumpleCuandoCoincide() {
    Hecho hecho = new HechoTestBuilder().conOrigen(Origen.CONTRIBUYENTE).build();
    FiltroHecho filtro = new FiltroOrigen(Origen.CONTRIBUYENTE);
    assertTrue(filtro.cumple(hecho));
  }
}
