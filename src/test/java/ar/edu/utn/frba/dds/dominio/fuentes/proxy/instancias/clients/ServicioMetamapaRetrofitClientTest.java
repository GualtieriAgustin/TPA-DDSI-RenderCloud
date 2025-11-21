package ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ar.edu.utn.frba.dds.dominio.fuentes.Origen;
import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos.FiltroHechoRequest;
import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos.HechoResponse;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.hechos.Ubicacion;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;

public class ServicioMetamapaRetrofitClientTest {
  private MetaMapaApi apiMock;
  private ServicioMetamapaRetrofitClient client;

  @BeforeEach
  void setUp() {
    apiMock = mock(MetaMapaApi.class);
    client = new ServicioMetamapaRetrofitClient("http://mocked-url.com", apiMock);
  }

  @Test
  void getHechos_CuandoRespuestaEsExitosa_DeberiaRetornarListaDeHechos() throws Exception {
    // Arrange
    FiltroHechoRequest filtro = new FiltroHechoRequest().conCategoria("incendio").create();

    HechoResponse hechoResponse = new HechoResponse(
        "Incendio",
        "Incendio en un edificio",
        "Categoría por defecto",
        new Ubicacion(10.0, 20.0),
        LocalDateTime.now().minusDays(3),
        LocalDateTime.now(),
        null // No multimedia en este caso
    );

    List<HechoResponse> mockedResponse = List.of(
        hechoResponse
    );
    Call<List<HechoResponse>> callMock = mock(Call.class);
    when(apiMock.getHechos(any(), any(), any(), any(), any(), any())).thenReturn(callMock);
    when(callMock.execute()).thenReturn(Response.success(mockedResponse));

    // Act
    List<Hecho> hechos = client.getHechos(filtro);

    // Assert
    assertEquals(1, hechos.size());
    Hecho hecho = hechos.get(0);
    assertEquals("Incendio", hecho.getTitulo());
    assertEquals("Incendio en un edificio", hecho.getDescripcion());
    assertEquals("Categoría por defecto", hecho.getCategoria());
    assertNotNull(hecho.getUbicacion());
    assertEquals(10.0, hecho.getUbicacion().getLatitud());
    assertEquals(20.0, hecho.getUbicacion().getLongitud());
    assertNotNull(hecho.getFechaSuceso());
    // Siempre es de tipo Proxy por el origen del hecho
    assertEquals(Origen.PROXY, hecho.getOrigen());
    assertNotNull(hecho.getFechaCarga());
  }

  @Test
  void getHechos_CuandoRespuestaNoEsExitosa_DeberiaLanzarExcepcion() throws Exception {
    // Arrange
    FiltroHechoRequest filtro = new FiltroHechoRequest().conCategoria("incendio").create();    Call<List<HechoResponse>> callMock = mock(Call.class);
    when(apiMock.getHechos(any(), any(), any(), any(), any(), any())).thenReturn(callMock);
    when(callMock.execute()).thenReturn(Response.error(500, ResponseBody.create(null, "Error simulado")));

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> client.getHechos(filtro));
    assertTrue(exception.getMessage().contains("Error en la respuesta"));
  }

  @Test
  void getHechos_CuandoHayErrorEnSolicitudHttp_DeberiaLanzarExcepcion() throws Exception {
    // Arrange
    FiltroHechoRequest filtro = new FiltroHechoRequest().conCategoria("incendio").create();
    Call<List<HechoResponse>> callMock = mock(Call.class);
    when(apiMock.getHechos(any(), any(), any(), any(), any(), any())).thenReturn(callMock);
    when(callMock.execute()).thenThrow(new IOException("Error de conexión"));

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> client.getHechos(filtro));
    assertTrue(exception.getMessage().contains("Error al realizar la solicitud HTTP"));
  }

  @Disabled("Este test solo debe ejecutarse manualmente")
  @Test
  void getHechos_CuandoSeConsultaAPIReal_DeberiaRetornarListaDeHechos() {
    // Arrange
    String baseUrl = "https://my-json-server.typicode.com/ciromartin/metamapa-test-api/";
    ServicioMetamapaRetrofitClient client = new ServicioMetamapaRetrofitClient(baseUrl);
    FiltroHechoRequest filtro = new FiltroHechoRequest();

    // Act
    List<Hecho> hechos = client.getHechos(filtro);

    // Assert
    assertNotNull(hechos);
    assertFalse(hechos.isEmpty());
    hechos.forEach(hecho -> assertNotNull(hecho.getTitulo()));
  }
}
