package ar.edu.utn.frba.dds.dominio.fuentes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeFuentes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RepositorioDeFuentesTest {

  @Mock
  EntityManager em;

  @Mock
  TypedQuery<FuenteEstatica> queryEstatica;

  @Mock
  TypedQuery<FuenteDinamica> queryDinamica;

  @Mock
  TypedQuery<Fuente> queryGenerica;

  private RepositorioDeFuentes repo() {
    // Subclase para inyectar el EntityManager mock y ejecutar transacciones inline
    return new RepositorioDeFuentes() {
      @Override
      public EntityManager entityManager() {
        return em;
      }

      @Override
      public void withTransaction(Runnable runnable) {
        // Ejecuta sin transacción para tests
        runnable.run();
      }
    };
  }

  @Test
  void buscarTodasEstaticas_debeRetornarLista() {
    RepositorioDeFuentes r = repo();
    when(em.createQuery(anyString(), eq(FuenteEstatica.class))).thenReturn(queryEstatica);

    FuenteEstatica f1 = mock(FuenteEstatica.class);
    when(queryEstatica.getResultList()).thenReturn(List.of(f1));

    var result = r.buscarPorTipo(FuenteEstatica.class);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertSame(f1, result.get(0));
    verify(em).createQuery(anyString(), eq(FuenteEstatica.class));
  }

  @Test
  void buscarTodasDinamicas_debeRetornarLista() {
    RepositorioDeFuentes r = repo();
    when(em.createQuery(anyString(), eq(FuenteDinamica.class))).thenReturn(queryDinamica);

    FuenteDinamica f1 = mock(FuenteDinamica.class);
    when(queryDinamica.getResultList()).thenReturn(List.of(f1));

    var result = r.buscarPorTipo(FuenteDinamica.class);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertSame(f1, result.get(0));
    verify(em).createQuery(anyString(), eq(FuenteDinamica.class));
  }

  @Test
  void buscarPorTipo_debePedirTypedQueryConLaClase() {
    RepositorioDeFuentes r = repo();
    @SuppressWarnings("unchecked")
    TypedQuery<FuenteEstatica> tq = mock(TypedQuery.class);
    when(em.createQuery(anyString(), eq(FuenteEstatica.class))).thenReturn(tq);

    FuenteEstatica expected = mock(FuenteEstatica.class);
    when(tq.getResultList()).thenReturn(List.of(expected));

    var result = r.buscarPorTipo(FuenteEstatica.class);

    assertEquals(1, result.size());
    assertSame(expected, result.get(0));
    verify(em).createQuery(anyString(), eq(FuenteEstatica.class));
  }

  @Test
  void guardar_y_eliminar_y_buscarPorId_funcionan() {
    RepositorioDeFuentes r = repo();

    Fuente entidad = mock(Fuente.class);
    doNothing().when(em).persist(entidad);
    doNothing().when(em).remove(entidad);

    // guardar debe llamar persist dentro de withTransaction
    r.guardar(entidad);
    verify(em, times(1)).persist(entidad);

    // eliminar debe llamar remove dentro de withTransaction
    r.eliminar(entidad);
    verify(em, times(1)).remove(entidad);

    // buscarPorId usa EntityManager.find
    when(em.find(Fuente.class, 42L)).thenReturn(entidad);
    Optional<Fuente> opt = r.buscarPorId(42L);
    assertTrue(opt.isPresent());
    assertSame(entidad, opt.get());
    verify(em).find(Fuente.class, 42L);
  }
}