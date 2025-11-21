package ar.edu.utn.frba.dds.dominio.solicitudes.procesador;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.exceptions.modificaciones.NoPuedeModificarHechoFueraDePlazoModificacionException;
import ar.edu.utn.frba.dds.dominio.solicitudes.exceptions.modificaciones.NoPuedeModificarHechoUsuarioNoCreadorException;
import ar.edu.utn.frba.dds.dominio.solicitudes.exceptions.modificaciones.NoPuedeModificarHechoUsuarioNoRegistradoException;
import ar.edu.utn.frba.dds.dominio.solicitudes.modificacion.ModificacionHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.modificacion.SolicitudModificacionHecho;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioHechoDinamico;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioHechoDinamicoMemoria;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitud;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitudesEnMemoria;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProcesadorDeSolicitudesDeModificacionTest {

  private ProcesadorDeSolicitudesDeModificacion procesador;
  private RepositorioHechoDinamico repoHechos;
  private RepositorioSolicitud repoSolicitudes;

  @BeforeEach
  void setUp() {
    this.repoHechos = new RepositorioHechoDinamicoMemoria();
    this.repoSolicitudes = new RepositorioSolicitudesEnMemoria();
    this.procesador = new ProcesadorDeSolicitudesDeModificacion(repoSolicitudes, repoHechos);
  }

  @Test
  void crearSolicitud_cuandoUsuarioNoRegistrado_debeLanzarExcepcion() {
    Usuario usuarioNoRegistrado = new Usuario("homero");
    Hecho hecho = new HechoTestBuilder().conUsuario(usuarioNoRegistrado).build();
    repoHechos.crear(hecho);

    assertThrows(
        NoPuedeModificarHechoUsuarioNoRegistradoException.class,
        () -> procesador.crearSolicitudDeModificacion(
            hecho.getId(), "justificacion", usuarioNoRegistrado, modificacion())
    );
  }

  @Test
  void crearSolicitud_cuandoUsuarioNoEsCreador_debeLanzarExcepcion() {
    Usuario lisa = new Usuario("lisa", true, 2L);
    Usuario homero = new Usuario("homero", true, 1L);
    Hecho hechoDeHomero = new HechoTestBuilder().conUsuario(homero).build();
    repoHechos.crear(hechoDeHomero);

    assertThrows(
        NoPuedeModificarHechoUsuarioNoCreadorException.class,
        () -> procesador.crearSolicitudDeModificacion(
            hechoDeHomero.getId(), "justificacion", lisa, modificacion())
    );
  }

  @Test
  void crearSolicitud_cuandoFueraDePlazo_debeLanzarExcepcion() {
    Usuario homero = new Usuario("homero", true, 1L);
    Hecho hechoFueraDePlazo = new HechoTestBuilder()
        .conUsuario(homero)
        .conFechaCarga(LocalDateTime.now().minusWeeks(2))
        .build();
    repoHechos.crear(hechoFueraDePlazo);

    assertThrows(
        NoPuedeModificarHechoFueraDePlazoModificacionException.class,
        () -> procesador.crearSolicitudDeModificacion(
            hechoFueraDePlazo.getId(), "justificacion", homero, modificacion())
    );
  }

  @Test
  void crearSolicitud_cuandoEsValida_debeCrearlaYPersistirla() {
    Usuario homero = new Usuario("homero", true, 1L);
    Hecho hecho = new HechoTestBuilder().conUsuario(homero).build();
    repoHechos.crear(hecho);

    SolicitudModificacionHecho solicitud = procesador.crearSolicitudDeModificacion(
        hecho.getId(), "justificacion valida", homero, modificacion());

    assertNotNull(solicitud);
    assertNotNull(repoSolicitudes.consultarPorId(solicitud.getId()));
  }

  private ModificacionHecho modificacion() {
    return new ModificacionHecho("nuevo titulo", null, null, null);
  }
}