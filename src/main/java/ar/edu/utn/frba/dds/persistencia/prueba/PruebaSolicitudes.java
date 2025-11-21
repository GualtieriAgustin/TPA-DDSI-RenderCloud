package ar.edu.utn.frba.dds.persistencia.prueba;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import ar.edu.utn.frba.dds.dominio.solicitudes.modificacion.ModificacionHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.modificacion.SolicitudModificacionHecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.observers.spam.DetectorDeSpamBasico;
import ar.edu.utn.frba.dds.dominio.solicitudes.observers.spam.DetectorSpamObserver;
import ar.edu.utn.frba.dds.dominio.solicitudes.procesador.ProcesadorDeSolicitudesDeBaja;
import ar.edu.utn.frba.dds.dominio.solicitudes.procesador.ProcesadorDeSolicitudesDeModificacion;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeUsuarios;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioDeHechos;
import ar.edu.utn.frba.dds.persistencia.solicitud.RepositorioSolicitudes;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class PruebaSolicitudes implements SimplePersistenceTest {
  public static void main(String[] args) {
    new PruebaSolicitudes().test();
  }

  public void test() {
    var repoSolicitudes = new RepositorioSolicitudes();
    var repoUsuarios = new RepositorioDeUsuarios();
    var repoHechos = new RepositorioDeHechos();

    var usuario = new Usuario("solicitante_test", true);
    var hecho = new Hecho(
        "Hecho de prueba para solicitudes",
        "Descripción del hecho de prueba.",
        "prueba",
        new Ubicacion(0.0, 0.0),
        LocalDateTime.now(),
        LocalDateTime.now(),
        Collections.emptyList(),
        usuario,
        Provincia.PROVINCIA_DESCONOCIDA
    );

    withTransaction(() -> {
      repoUsuarios.registrar(usuario);
      repoHechos.crear(hecho);
    });

    var criterio = new CriterioDePertenencia();
    var detectorSpamObserver = new DetectorSpamObserver(new DetectorDeSpamBasico());
    var procesadorDeSolicitudes =
        new ProcesadorDeSolicitudesDeBaja(repoSolicitudes, List.of(detectorSpamObserver));
    procesadorDeSolicitudes.crearSolicitudDeBaja(criterio, "spam " + "a".repeat(500), usuario);
    procesadorDeSolicitudes.crearSolicitudDeBaja(criterio, "a".repeat(500), usuario); // pendiente
    var solicitud = procesadorDeSolicitudes.crearSolicitudDeBaja(
        criterio, "a".repeat(500), usuario
    );
    procesadorDeSolicitudes.aceptar(solicitud);

    var procesadorDeSolicitudesModificacion =
        new ProcesadorDeSolicitudesDeModificacion(repoSolicitudes, repoHechos);
    var modificaciones = new ModificacionHecho("Nuevo título sugerido", null, null, null);
    var solicitudDeModificacion = procesadorDeSolicitudesModificacion.crearSolicitudDeModificacion(
        hecho.getId(), "Justificación de modificación", usuario, modificaciones);
    procesadorDeSolicitudesModificacion.aceptar(solicitudDeModificacion);

    System.out.println("--- Solicitudes persistidas en la base de datos ---");
    var solicitudesRecuperadas = repoSolicitudes.consultarTodas();

    System.out.println("Total de solicitudes encontradas: " + solicitudesRecuperadas.size());
    solicitudesRecuperadas.forEach(s -> {
      System.out.println("  - ID: " + s.getId() + ", Tipo: " + s.getClass().getSimpleName());
      if (s instanceof SolicitudModificacionHecho sm) {
        System.out.println("    Sugerencia de título: " + sm.getModificacionHecho().getTitulo());
      }
    });

    System.out.println("\n--- Consultando solicitudes pendientes ---");
    var solicitudesPendientes = repoSolicitudes.consultarPendientes();
    System.out.println("Total de solicitudes pendientes: " + solicitudesPendientes.size());
  }
}
