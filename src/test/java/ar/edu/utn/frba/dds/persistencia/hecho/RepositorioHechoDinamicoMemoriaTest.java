package ar.edu.utn.frba.dds.persistencia.hecho;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroCategoria;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroHecho;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RepositorioHechoDinamicoMemoriaTest {

  @Test
  public void consultarTodos_CuandoRepositorioRecienCreado_DebeRetornarListaVacia() {
    RepositorioHechoDinamico repositorio = new RepositorioHechoDinamicoMemoria();

    var hechos = repositorio.consultarTodos();

    assertTrue(hechos.isEmpty());
  }

  @Test
  public void crear_CuandoSeAgregaHecho_DebeAgregarloAlRepositorio() {
    RepositorioHechoDinamico repositorio = new RepositorioHechoDinamicoMemoria();
    Usuario usuario = new Usuario("CosmeFulanito", true);

    Hecho hecho = new HechoTestBuilder()
        .conTitulo("Incendio")
        .conUsuario(usuario)
        .build();

    repositorio.crear(hecho);

    List<Hecho> hechos = repositorio.consultarTodos();
    assertEquals(1, hechos.size());
    assertTrue(hechos.contains(hecho));
  }

  @Test
  public void consultarTodos_CuandoHayVariosHechos_DebeDevolverTodosLosHechos() {
    RepositorioHechoDinamico repositorio = new RepositorioHechoDinamicoMemoria();
    Usuario usuario = new Usuario("CosmeFulanito", true);

    Hecho hecho1 = new HechoTestBuilder()
        .conTitulo("Incendio")
        .conUsuario(usuario)
        .build();
    Hecho hecho2 = new HechoTestBuilder()
        .conTitulo("Robo")
        .conUsuario(usuario)
        .build();

    repositorio.crear(hecho1);
    repositorio.crear(hecho2);

    List<Hecho> hechos = repositorio.consultarTodos();

    assertEquals(2, hechos.size());
    assertTrue(hechos.contains(hecho1));
    assertTrue(hechos.contains(hecho2));
  }

  @Test
  public void consultarPorId_CuandoExisteHecho_DebeRetornarElHecho() {
    RepositorioHechoDinamico repositorio = new RepositorioHechoDinamicoMemoria();
    Usuario usuario = new Usuario("Milhouse", true);

    Hecho hecho = new HechoTestBuilder()
        .conTitulo("Corte de luz")
        .conUsuario(usuario)
        .build();

    repositorio.crear(hecho);

    Hecho recuperado = repositorio.consultarPorId(hecho.getId());

    assertEquals(hecho, recuperado);
  }

  @Test
  public void consultarPorId_CuandoNoExisteHecho_DebeRetornarNull() {
    RepositorioHechoDinamico repositorio = new RepositorioHechoDinamicoMemoria();

    Hecho recuperado = repositorio.consultarPorId(0L);

    assertNull(recuperado);
  }

  @Test
  public void consultarPorUsuario_CuandoUsuarioCargoUnHecho_DebeRetornarSoloEseHecho() {
    RepositorioHechoDinamico repositorio = new RepositorioHechoDinamicoMemoria();
    Usuario homero = new Usuario("Homero", true, 1L);
    Usuario lisa = new Usuario("Lisa", true, 2L);

    Hecho hechoHomero = new HechoTestBuilder()
        .conCategoria("Emergencia")
        .conUsuario(homero)
        .build();
    Hecho hechoLisa = new HechoTestBuilder()
        .conCategoria("Obra Pública")
        .conUsuario(lisa)
        .build();

    repositorio.crear(hechoHomero);
    repositorio.crear(hechoLisa);

    List<Hecho> filtrados = repositorio.consultarPorUsuario(homero.getId());

    assertEquals(1, filtrados.size());
    assertTrue(filtrados.contains(hechoHomero));
    assertFalse(filtrados.contains(hechoLisa));
  }

  @Test
  public void consultarPorCriterio_CuandoUnHechoCumple_DebeRetornarEseHecho() {
    RepositorioHechoDinamico repositorio = new RepositorioHechoDinamicoMemoria();

    Usuario marge = new Usuario("Marge", true);

    Hecho hecho = new HechoTestBuilder()
        .conCategoria("Emergencia")
        .conUsuario(marge)
        .build();
    repositorio.crear(hecho);

    FiltroHecho filtro = new FiltroCategoria("Emergencia");
    List<Hecho> filtrados = repositorio.consultarPorCriterio(new CriterioDePertenencia(filtro));

    assertEquals(1, filtrados.size());
    assertTrue(filtrados.contains(hecho));
  }

  @Test
  public void consultarPorCriterio_CuandoNingunHechoCumple_DebeRetornarListaVacia() {
    RepositorioHechoDinamico repositorio = new RepositorioHechoDinamicoMemoria();

    Usuario marge = new Usuario("Marge", true);

    Hecho hecho1 = new HechoTestBuilder()
        .conCategoria("Accidente")
        .conUsuario(marge)
        .build();

    repositorio.crear(hecho1);

    FiltroHecho filtro = new FiltroCategoria("Emergencia");
    List<Hecho> filtrados = repositorio.consultarPorCriterio(new CriterioDePertenencia(filtro));

    assertTrue(filtrados.isEmpty());
  }

  @Test
  public void actualizar_CuandoSeActualizaUnHecho_DebeReflejarElCambio() {
    RepositorioHechoDinamico repositorio = new RepositorioHechoDinamicoMemoria();

    Usuario bart = new Usuario("Bart", true);

    Hecho hecho = new HechoTestBuilder()
        .conTitulo("Incendio")
        .conUsuario(bart)
        .build();


    repositorio.crear(hecho);

    hecho.setTitulo("Incendio Actualizado");

    repositorio.actualizar(hecho);

    Hecho recuperado = repositorio.consultarPorId(hecho.getId());
    assertEquals("Incendio Actualizado", recuperado.getTitulo());
  }

}