package ar.edu.utn.frba.dds.dominio.estadisticas;

import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import org.mockito.Mock;
import javax.persistence.EntityManager;
import javax.persistence.Query;

class EstadisticasServiceTest implements SimplePersistenceTest {

    private EstadisticasServiceImpl service;

    @Mock
    private EntityManager em;

    @Mock
    private Query queryMock;


/*
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new EstadisticasService();
        service.setEntityManager(em);
    }

    @Test
    void testEjecutarConQueryGenerica() {
        List<Object[]> resultados = Collections.singletonList(new Object[]{"Buenos Aires", 10L});

        when(em.createNativeQuery(EstadisticaQuery.PROVINCIA_CON_MAS_HECHOS.getQuery()))
                .thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(resultados);

        List<Object[]> res = service.ejecutar(EstadisticaQuery.PROVINCIA_CON_MAS_HECHOS);

        assertEquals(1, res.size());
        assertEquals("Buenos Aires", res.get(0)[0]);
        assertEquals(10L, res.get(0)[1]);
    }

    @Test
    void testProvinciaConMasHechos() {
        when(em.createNativeQuery(EstadisticaQuery.PROVINCIA_CON_MAS_HECHOS.getQuery()))
                .thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(Collections.singletonList(new Object[]{"Cordoba", 5L}));

        List<Object[]> res = service.provinciaConMasHechos();
        assertEquals("Cordoba", res.get(0)[0]);
        assertEquals(5L, res.get(0)[1]);
    }

    @Test
    void testProvinciaConMasHechosPorCategoria() {
        when(em.createNativeQuery(EstadisticaQuery.PROVINCIA_CON_MAS_HECHOS_POR_CATEGORIA.getQuery()))
                .thenReturn(queryMock);
        when(queryMock.setParameter("categoria", "Robo")).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(Collections.singletonList(new Object[]{"Buenos Aires", 3L}));

        List<Object[]> res = service.provinciaConMasHechosPorCategoria("Robo");
        assertEquals("Buenos Aires", res.get(0)[0]);
        assertEquals(3L, res.get(0)[1]);
    }

    @Test
    void testHoraConMasHechosPorCategoria() {
        when(em.createNativeQuery(EstadisticaQuery.HORA_CON_MAS_HECHOS_POR_CATEGORIA.getQuery()))
                .thenReturn(queryMock);
        when(queryMock.setParameter("categoria", "Fraude")).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(Collections.singletonList(new Object[]{14, 7L}));

        List<Object[]> res = service.horaConMasHechosPorCategoria("Fraude");
        assertEquals(14, res.get(0)[0]);
        assertEquals(7L, res.get(0)[1]);
    }

    @Test
    void testCategoriaConMasHechos() {
        List<Object[]> resultados = Collections.singletonList(new Object[]{"Robo", 12L});

        when(em.createNativeQuery(EstadisticaQuery.CATEGORIA_CON_MAS_HECHOS.getQuery()))
                .thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(resultados);

        List<Object[]> res = service.categoriaConMasHechos();

        assertEquals(1, res.size());
        assertEquals("Robo", res.get(0)[0]);
        assertEquals(12L, res.get(0)[1]);
    }

    @Test
    void testCantidadSolicitudesSpam() {
        when(em.createNativeQuery(EstadisticaQuery.CANTIDAD_SOLICITUDES_SPAM.getQuery()))
                .thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenReturn(8L);

        Long res = service.cantidadSolicitudesSpam();
        assertEquals(8L, res);
    }*/
}