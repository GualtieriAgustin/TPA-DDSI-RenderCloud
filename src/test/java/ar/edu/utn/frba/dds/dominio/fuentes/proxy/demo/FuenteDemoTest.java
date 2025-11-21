package ar.edu.utn.frba.dds.dominio.fuentes.proxy.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ar.edu.utn.frba.dds.dominio.fuentes.Origen;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class FuenteDemoTest {

  @Test
  public void testIncorporarNuevosHechos() {
    ObtenedorDeHechos obtenedorDeHechos = mock(ObtenedorDeHechos.class);
    when(obtenedorDeHechos.getNuevosHechos(any())).thenReturn(
        List.of(crearHecho(), crearHecho(), crearHecho())
    );
    FuenteDemo fuenteDemo = new FuenteDemo(obtenedorDeHechos);

    fuenteDemo.incorporarNuevosHechos(LocalDateTime.parse("2025-05-05T12:15"));

    assertEquals(3, fuenteDemo.getHechos().size());
  }

  @Test
  public void testIncorporarNuevosHechosMultiplesVeces() {
    ObtenedorDeHechos obtenedorDeHechos = mock(ObtenedorDeHechos.class);
    when(obtenedorDeHechos.getNuevosHechos(any())).thenReturn(
        List.of(crearHecho())
    );
    FuenteDemo fuenteDemo = new FuenteDemo(obtenedorDeHechos);

    fuenteDemo.incorporarNuevosHechos(LocalDateTime.parse("2025-05-05T12:15"));
    fuenteDemo.incorporarNuevosHechos(LocalDateTime.parse("2025-05-05T12:15"));

    assertEquals(2, fuenteDemo.getHechos().size());
  }

  @Test
  public void testIncorporarNuevosHechosNoTraeHechosNuevos() {
    ObtenedorDeHechos obtenedorDeHechos = mock(ObtenedorDeHechos.class);
    when(obtenedorDeHechos.getNuevosHechos(any())).thenReturn(Collections.emptyList());
    FuenteDemo fuenteDemo = new FuenteDemo(obtenedorDeHechos);

    fuenteDemo.incorporarNuevosHechos(LocalDateTime.parse("2025-05-05T12:15"));
    fuenteDemo.incorporarNuevosHechos(LocalDateTime.parse("2025-05-05T12:15"));

    assertEquals(0, fuenteDemo.getHechos().size());
  }

  private Hecho crearHecho() {
    LocalDateTime unaFecha = LocalDateTime.now();
    return new Hecho(
        "unHecho",
        "unaDescripcion",
        "unaCategoria",
        new Ubicacion(0.0, 0.0),
        unaFecha,
        Origen.PROXY,
        unaFecha,
        Provincia.PROVINCIA_DESCONOCIDA
    );
  }
}
