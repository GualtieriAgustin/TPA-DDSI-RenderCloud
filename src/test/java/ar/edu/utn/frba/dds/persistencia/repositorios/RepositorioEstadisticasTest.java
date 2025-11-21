package ar.edu.utn.frba.dds.persistencia.repositorios;

import ar.edu.utn.frba.dds.dominio.estadisticas.EstadisticaResultado;
import ar.edu.utn.frba.dds.dominio.estadisticas.RepositorioEstadisticas;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RepositorioEstadisticasTest implements SimplePersistenceTest {

    private RepositorioEstadisticas repositorioEstadisticas;

    @BeforeEach
    void setUp() {
        repositorioEstadisticas = new RepositorioEstadisticas();
    }

    @Test
    void sePuedeRegistrarYRecuperarUnaEstadistica() {
        EstadisticaResultado estadistica = new EstadisticaResultado("AGUSTIN", "RESULTADO", null, LocalDateTime.now(), LocalDateTime.now());
        repositorioEstadisticas.registrar(estadistica);

        EstadisticaResultado recuperada = repositorioEstadisticas.obtener(estadistica.getId());
        assertNotNull(recuperada);
        assertEquals("AGUSTIN", recuperada.getNombre());
    }

    @Test
    void sePuedenConsultarTodasLasEstadisticas() {
        repositorioEstadisticas.registrar(new EstadisticaResultado("AGUSTIN", "RESULTADO", "CATEGORIA", LocalDateTime.now(), LocalDateTime.now()));
        repositorioEstadisticas.registrar(new EstadisticaResultado("JUAN", "RESULTADO2", "CATEGORIA2", LocalDateTime.now(), LocalDateTime.now()));

        List<EstadisticaResultado> estadisticas = repositorioEstadisticas.obtenerTodasLasEstadisticas();
        assertEquals(2, estadisticas.size());
    }

    @Test
    void sePuedeObtenerLaUltimaEstadistica() {
        repositorioEstadisticas.registrar(new EstadisticaResultado("AGUSTIN", "RESULTADO", "CATEGORIA", LocalDateTime.now(), LocalDateTime.now()));
        EstadisticaResultado ultima = repositorioEstadisticas.registrar(
            new EstadisticaResultado("JUAN", "RESULTADO2", "CATEGORIA2", LocalDateTime.now(), LocalDateTime.now())
        );

        EstadisticaResultado recuperada = repositorioEstadisticas.obtenerUltimaEstadistica();
        assertNotNull(recuperada);
        assertEquals(ultima.getId(), recuperada.getId());
    }
}
