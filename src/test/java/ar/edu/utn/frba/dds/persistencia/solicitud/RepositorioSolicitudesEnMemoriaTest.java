package ar.edu.utn.frba.dds.persistencia.solicitud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroTitulo;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.EstadoSolicitud;
import ar.edu.utn.frba.dds.dominio.solicitudes.baja.SolicitudBajaHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.modificacion.ModificacionHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.modificacion.SolicitudModificacionHecho;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioHechoDinamico;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioHechoDinamicoMemoria;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositorioSolicitudesEnMemoriaTest {

  private RepositorioHechoDinamico repoHechos;
  private RepositorioSolicitud repoSolicitudes;
  private Usuario usuarioRegistrado;
  private Hecho hecho;

  @BeforeEach
  void setUp() {
    repoHechos = new RepositorioHechoDinamicoMemoria();
    repoSolicitudes = new RepositorioSolicitudesEnMemoria();
    usuarioRegistrado = new Usuario("homero", true, 1L);
    hecho = new HechoTestBuilder()
        .conTitulo("Incendio")
        .conUsuario(usuarioRegistrado)
        .build();
    repoHechos.crear(hecho);
  }

  @Test
  void sePuedeCrearYConsultarUnaSolicitudDeModificacion() {
    var solicitud = new SolicitudModificacionHecho(1L, "justificacion", usuarioRegistrado, modificacion());

    repoSolicitudes.crear(solicitud);

    assertEquals(1, repoSolicitudes.consultarTodas().size());
    var solicitudRecuperada = repoSolicitudes.consultarModificacionPorId(solicitud.getId());
    assertNotNull(solicitudRecuperada);
    assertEquals(solicitud.getId(), solicitudRecuperada.getId());
  }

  @Test
  void sePuedeCrearYConsultarUnaSolicitudDeBaja() {
    var criterio = new CriterioDePertenencia(new FiltroTitulo("Incendio"));
    var solicitud = new SolicitudBajaHecho(criterio, "motivo de baja".repeat(50), usuarioRegistrado);

    repoSolicitudes.crear(solicitud);

    assertEquals(1, repoSolicitudes.consultarTodas().size());
    var solicitudRecuperada = repoSolicitudes.consultarBajaPorId(solicitud.getId());
    assertNotNull(solicitudRecuperada);
    assertEquals(solicitud.getId(), solicitudRecuperada.getId());
  }

  @Test
  void consultarPendientesDevuelveSoloLasPendientes() {
    var solicitudMod = new SolicitudModificacionHecho(1L, "justificacion", usuarioRegistrado, new ModificacionHecho("a", null, null, null));
    var solicitudBaja = new SolicitudBajaHecho(new CriterioDePertenencia(), "motivo".repeat(100), usuarioRegistrado);
    solicitudBaja.aceptar();

    repoSolicitudes.crear(solicitudMod);
    repoSolicitudes.crear(solicitudBaja);

    List<SolicitudModificacionHecho> pendientes = repoSolicitudes.consultarPendientes()
        .stream()
        .filter(s -> s instanceof SolicitudModificacionHecho)
        .map(s -> (SolicitudModificacionHecho) s).collect(Collectors.toList());

    assertEquals(1, pendientes.size());
    assertEquals(EstadoSolicitud.PENDIENTE, pendientes.get(0).getEstado());
  }

  @Test
  void consultarPorUsuarioDevuelveSoloLasDeEseUsuario() {
    var solicitudUsuario1 = new SolicitudModificacionHecho(1L, "justificacion", usuarioRegistrado, modificacion());
    var otroUsuario = new Usuario("marge", true, 2L);
    var solicitudUsuario2 = new SolicitudModificacionHecho(1L, "otra justificacion", otroUsuario, modificacion());

    repoSolicitudes.crear(solicitudUsuario1);
    repoSolicitudes.crear(solicitudUsuario2);

    List<SolicitudModificacionHecho> solicitudesEncontradas = repoSolicitudes.consultarPorUsuario(usuarioRegistrado.getId());

    assertEquals(1, solicitudesEncontradas.size());
    assertEquals(solicitudUsuario1.getId(), solicitudesEncontradas.get(0).getId());
  }

  private ModificacionHecho modificacion() {
    return new ModificacionHecho("a", null, null, null);
  }
}