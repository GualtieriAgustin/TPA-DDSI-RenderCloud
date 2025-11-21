package ar.edu.utn.frba.dds.dominio.fuentes.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.fuentes.Agregador;
import ar.edu.utn.frba.dds.dominio.fuentes.FuenteDinamica;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioHechoDinamico;
import ar.edu.utn.frba.dds.utils.builders.HechoTestBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FuenteCacheableTest {

    @Mock
    private RepositorioHechoDinamico repositorio;

    @Mock
    private RepositorioHechoDinamico repositorio2;
    
    @Mock
    private CriterioDePertenencia criterio;

    private FuenteDinamica fuenteDinamica;
    private Agregador agregador;
    private Hecho hechoTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repositorio = mock(RepositorioHechoDinamico.class);
        repositorio2 = mock(RepositorioHechoDinamico.class);
        fuenteDinamica = new FuenteDinamica(repositorio);
        agregador = new Agregador();
        hechoTest = new HechoTestBuilder()
            .conTitulo("Test")
            .conDescripcion("Descripción test")
            .build();
    }

    @Test
    void fuenteDinamica_DeberiaUsarCacheEnSegundaConsulta() {
        // Se verifica que FuenteDinamica solo consulte al repositorio una vez, y que en
        // la segunda llamada con el mismo criterio use caché.
        when(repositorio.consultarPorCriterio(criterio)).thenReturn(List.of(hechoTest));

        List<Hecho> primeraConsulta = fuenteDinamica.getHechosPorCriterio(criterio);
        List<Hecho> segundaConsulta = fuenteDinamica.getHechosPorCriterio(criterio);

        verify(repositorio, times(1)).consultarPorCriterio(criterio);
        assertEquals(primeraConsulta, segundaConsulta);
    }

    @Test
    void agregador_DeberiaUsarCacheParaCadaFuente() {

        FuenteDinamica fuente1 = new FuenteDinamica(repositorio);
        FuenteDinamica fuente2 = new FuenteDinamica(repositorio2);

        when(repositorio.consultarPorCriterio(criterio))
            .thenReturn(List.of(hechoTest));
        when(repositorio2.consultarPorCriterio(criterio))
            .thenReturn(List.of(hechoTest));

        agregador.agregarFuente(fuente1);
        agregador.agregarFuente(fuente2);

        //Primera consulta (debería consultar ambos repositorios)
        List<Hecho> primeraConsulta = agregador.getHechosPorCriterio(criterio);

        //Verificamos que ambos repositorios hayan sido consultados una vez
        verify(repositorio, times(1)).consultarPorCriterio(criterio);
        verify(repositorio2, times(1)).consultarPorCriterio(criterio);

        // Limpiamos las interacciones para verificar solo lo que ocurre después
        clearInvocations(repositorio, repositorio2);

        //Segunda consulta (debería usar caché, sin consultar los repositorios)
        List<Hecho> segundaConsulta = agregador.getHechosPorCriterio(criterio);

        //Verificamos que no se consultaron nuevamente
        verify(repositorio, never()).consultarPorCriterio(criterio);
        verify(repositorio2, never()).consultarPorCriterio(criterio);

        //Verificamos que los resultados de ambas consultas sean iguales
        assertEquals(primeraConsulta, segundaConsulta);
    }


    @Test
    void todasLasFuentes_DeberianUsarCache() {
        FuenteDinamica fuenteDinamica = new FuenteDinamica(repositorio);
        when(repositorio.consultarPorCriterio(criterio))
            .thenReturn(List.of(hechoTest));

        //Consultas a la fuente dinámica
        List<Hecho> resultadoDinamico1 = fuenteDinamica.getHechosPorCriterio(criterio);
        List<Hecho> resultadoDinamico2 = fuenteDinamica.getHechosPorCriterio(criterio);

        verify(repositorio, times(1)).consultarPorCriterio(criterio);
        assertEquals(resultadoDinamico1, resultadoDinamico2);
    }

    @Test
    void agregador_NoDeberiaLlamarARepositorio_CuandoUsaCache() {

        when(repositorio.consultarPorCriterio(criterio))
            .thenReturn(List.of(hechoTest));
        agregador.agregarFuente(fuenteDinamica);

        List<Hecho> primeraConsulta = agregador.getHechosPorCriterio(criterio);

        clearInvocations(repositorio);
        
        List<Hecho> segundaConsulta = agregador.getHechosPorCriterio(criterio);

        verify(repositorio, never()).consultarPorCriterio(criterio);
        assertEquals(primeraConsulta, segundaConsulta);
    }
}