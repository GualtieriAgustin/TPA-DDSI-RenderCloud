package ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients;

import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.ServicioMetamapa;
import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos.FiltroHechoRequest;
import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos.HechoResponse;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.io.IOException;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Entity
@DiscriminatorValue("INSTANCIA_METAMAPA")
public class ServicioMetamapaRetrofitClient extends ServicioMetamapa {
  @Transient
  private final MetaMapaApi api;
  private final String baseUrl;

  public ServicioMetamapaRetrofitClient(String baseUrl) {
    this.baseUrl = baseUrl;
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(this.baseUrl)
        .addConverterFactory(GsonConverterFactory.create(GsonConfig.createGson()))
        .build();
    this.api = retrofit.create(MetaMapaApi.class);
  }

  public ServicioMetamapaRetrofitClient(String baseUrl, MetaMapaApi api) {
    this.baseUrl = baseUrl;
    this.api = api;
  }

  @Override
  public List<Hecho> getHechos(FiltroHechoRequest filtro) {
    Call<List<HechoResponse>> call = api.getHechos(
        filtro.getCategoria(),
        filtro.getFechaReporteDesde(),
        filtro.getFechaReporteHasta(),
        filtro.getFechaAcontecimientoDesde(),
        filtro.getFechaAcontecimientoHasta(),
        filtro.getUbicacion());
    try {
      Response<List<HechoResponse>> response = call.execute();

      if (!response.isSuccessful() || response.body() == null) {
        throw new RuntimeException(
            "Error en la respuesta: " + response.code() + " - " + response.message());
      }

      return response
          .body()
          .stream()
          .map(HechoResponse::toDomain)
          .toList();
    } catch (IOException e) {
      throw new RuntimeException("Error al realizar la solicitud HTTP", e);
    }
  }
}