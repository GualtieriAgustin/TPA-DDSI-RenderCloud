package ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients;

import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos.HechoResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MetaMapaApi {
  @GET("hechos")
  Call<List<HechoResponse>> getHechos(
      @Query("categoria") String categoria,
      @Query("fecha_reporte_desde") String fechaReporteDesde,
      @Query("fecha_reporte_hasta") String fechaReporteHasta,
      @Query("fecha_acontecimiento_desde") String fechaAcontecimientoDesde,
      @Query("fecha_acontecimiento_hasta") String fechaAcontecimientoHasta,
      @Query("ubicacion") String ubicacion
  );
}
