package ar.edu.utn.frba.dds.dominio.fuentes;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroCategoria;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroHecho;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioHechoDinamico;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioHechoDinamicoMemoria;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import java.util.List;
import org.junit.jupiter.api.Test;

public class FuenteDinamicaTest {

  @Test
  public void getHechos_CuandoRepositorioVacio_DebeRetornarListaVacia() {

    RepositorioHechoDinamico repositorio = new RepositorioHechoDinamicoMemoria();
    FuenteDinamica fuente = new FuenteDinamica(repositorio);

    assertTrue(fuente.getHechos().isEmpty());
  }


  @Test
  public void getHechos_CuandoHayHechosEnRepositorio_DebeRetornarTodosLosHechos() {
    RepositorioHechoDinamico repositorio = new RepositorioHechoDinamicoMemoria();
    FuenteDinamica fuente = new FuenteDinamica(repositorio);
    Usuario usuarioRegistrado = new Usuario("Ciro", true);
    Usuario usuarioNoRegistrado = new Usuario("Zoe");

    Hecho hecho1 = new HechoTestBuilder()
        .conTitulo("Incendio")
        .conUsuario(usuarioRegistrado)
        .build();
    Hecho hecho2 = new HechoTestBuilder()
        .conTitulo("Robo")
        .conUsuario(usuarioNoRegistrado)
        .build();

    repositorio.crear(hecho1);
    repositorio.crear(hecho2);

    List<Hecho> hechos = fuente.getHechos();

    assertTrue(hechos.contains(hecho1));
    assertTrue(hechos.contains(hecho2));
    assertEquals(2, hechos.size());
  }

  @Test
  public void getHechosPorCriterio_CuandoFiltroPorCategoria_DebeRetornarSoloHechosFiltrados() {

    RepositorioHechoDinamico repositorio = new RepositorioHechoDinamicoMemoria();
    FuenteDinamica fuente = new FuenteDinamica(repositorio);
    Usuario usuarioRegistrado = new Usuario("Ciro", true);

    Hecho hechoEmergencia = new HechoTestBuilder()
        .conTitulo("Incendio en calle falsa")
        .conCategoria("Emergencia")
        .conUsuario(usuarioRegistrado)
        .build();

    Hecho hechoInfraestructura = new HechoTestBuilder()
        .conTitulo("Obra pública")
        .conCategoria("Infraestructura")
        .conUsuario(usuarioRegistrado)
        .build();

    repositorio.crear(hechoEmergencia);
    repositorio.crear(hechoInfraestructura);

    FiltroHecho filtro = new FiltroCategoria("Emergencia");

    List<Hecho> filtrados = fuente.getHechosPorCriterio(new CriterioDePertenencia(filtro));

    assertTrue(filtrados.contains(hechoEmergencia));
    assertFalse(filtrados.contains(hechoInfraestructura));
  }

  @Test
  public void getHechosPorUsuario_CuandoUsuarioCargoUnHecho_DebeRetornarSoloEseHecho() {
    RepositorioHechoDinamico repositorio = new RepositorioHechoDinamicoMemoria();
    FuenteDinamica fuente = new FuenteDinamica(repositorio);
    Usuario ciro = new Usuario("Ciro", true, 1L);
    Usuario zoe = new Usuario("Zoe", false, 2L);

    Hecho hechoUsuarioCiro = new HechoTestBuilder()
        .conCategoria("Emergencia")
        .conUsuario(ciro)
        .build();
    Hecho hechoUsuarioZoe = new HechoTestBuilder()
        .conCategoria("Obra Pública")
        .conUsuario(zoe)
        .build();

    repositorio.crear(hechoUsuarioCiro);
    repositorio.crear(hechoUsuarioZoe);

    List<Hecho> filtrados = fuente.getHechosPorUsuario(ciro.getId());


    assertEquals(1, filtrados.size());
    assertTrue(filtrados.contains(hechoUsuarioCiro));
    assertFalse(filtrados.contains(hechoUsuarioZoe));
  }

}