package ar.edu.utn.frba.dds.dominio.solicitudes.procesador;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroTitulo;
import ar.edu.utn.frba.dds.dominio.solicitudes.EstadoSolicitud;
import ar.edu.utn.frba.dds.dominio.solicitudes.baja.SolicitudBajaHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.observers.spam.DetectorDeSpam;
import ar.edu.utn.frba.dds.dominio.solicitudes.observers.spam.DetectorDeSpamBasico;
import ar.edu.utn.frba.dds.dominio.solicitudes.observers.spam.DetectorSpamObserver;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitud;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitudesEnMemoria;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProcesadorDeSolicitudesDeBajaObserverTest {

  private ProcesadorDeSolicitudesDeBaja procesador;
  private RepositorioSolicitud repoSolicitudes;

  @BeforeEach
  void setUp() {
    // 1. Crear los componentes base
    this.repoSolicitudes = new RepositorioSolicitudesEnMemoria();
    DetectorDeSpam detectorDeSpam = new DetectorDeSpamBasico();

    // 2. Crear el observer (listener)
    DetectorSpamObserver rechazarSpamListener = new DetectorSpamObserver(detectorDeSpam);

    // 3. Crear el procesador, inyectándole el observer
    this.procesador = new ProcesadorDeSolicitudesDeBaja(repoSolicitudes, List.of(rechazarSpamListener));
  }

  @Test
  void crearSolicitud_cuandoDescripcionEsSpam_elObserverLaRechaza() {
    // Arrange
    CriterioDePertenencia criterio = new CriterioDePertenencia(
        new FiltroTitulo("Hecho de prueba")
    );
    String descripcionSpam = "contiene spam".repeat(500);
    Usuario usuario = new Usuario("test");

    // Act
    SolicitudBajaHecho solicitudCreada = procesador.crearSolicitudDeBaja(criterio, descripcionSpam, usuario);

    // Assert
    // La solicitud se crea, pero el listener la rechaza inmediatamente.
    SolicitudBajaHecho solicitudEnRepo = repoSolicitudes.consultarBajaPorId(solicitudCreada.getId());
    assertEquals(EstadoSolicitud.RECHAZADA, solicitudEnRepo.getEstado());
  }

  @Test
  void crearSolicitud_cuandoDescripcionNoEsSpam_laSolicitudPermanecePendiente() {
    // Arrange
    CriterioDePertenencia criterio = new CriterioDePertenencia(
        new FiltroTitulo("Hecho de prueba")
    );
    String descripcionValida = "motivo válido".repeat(500);
    Usuario usuario = new Usuario("test");

    // Act
    SolicitudBajaHecho solicitudCreada = procesador.crearSolicitudDeBaja(criterio, descripcionValida, usuario);

    // Assert
    // La solicitud se crea y, como no es spam, permanece en estado PENDIENTE.
    SolicitudBajaHecho solicitudEnRepo = repoSolicitudes.consultarBajaPorId(solicitudCreada.getId());
    assertEquals(EstadoSolicitud.PENDIENTE, solicitudEnRepo.getEstado());
  }
}