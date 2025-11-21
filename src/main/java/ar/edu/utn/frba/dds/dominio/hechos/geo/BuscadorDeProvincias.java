package ar.edu.utn.frba.dds.dominio.hechos.geo;


import ar.edu.utn.frba.dds.dominio.hechos.Provincia;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.jts2geojson.GeoJSONReader;

public class BuscadorDeProvincias {

  private final List<ProvinciaConGeometria> provincias = new ArrayList<>();

  public BuscadorDeProvincias(String filePath) {
    cargarProvinciasDesdeGeoJson(filePath);
  }

  public Provincia buscarProvinciaPorCoordenadas(double lat, double lon) {
    GeometryFactory geomFactory = new GeometryFactory();
    Point punto = geomFactory.createPoint(new Coordinate(lon, lat));

    for (ProvinciaConGeometria provincia : this.provincias) {
      if (provincia.geometria().contains(punto)) {
        return provincia.provincia();
      }
    }
    return Provincia.PROVINCIA_DESCONOCIDA;
  }

  private void cargarProvinciasDesdeGeoJson(String filePath) {
    String geoJsonText;
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath))  {
      if (is == null) {
        throw new IllegalArgumentException("No se encontró provincias.geojson en resources");
      }
      geoJsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);

      ObjectMapper mapper = new ObjectMapper();
      FeatureCollection featureCollection = mapper.readValue(geoJsonText, FeatureCollection.class);

      GeoJSONReader geoJsonReader = new GeoJSONReader();
      GeometryFactory geometryFactory = new GeometryFactory();

      for (Feature feature : featureCollection.getFeatures()) {
        String nombreProvincia = (String) feature.getProperties().get("nombre");

        Geometry geom = geoJsonReader.read(feature.getGeometry());
        MultiPolygon multiPolygon = toMultiPolygon(geom, geometryFactory);

        this.provincias.add(new ProvinciaConGeometria(nombreProvincia, multiPolygon));
      }
    } catch (Exception e) {
      throw new RuntimeException("Error al cargar el archivo GeoJSON de provincias", e);
    }
  }

  private MultiPolygon toMultiPolygon(Geometry geom, GeometryFactory geometryFactory) {
    if (geom instanceof MultiPolygon) {
      return (MultiPolygon) geom;
    } else if (geom instanceof Polygon) {
      return geometryFactory.createMultiPolygon(new Polygon[]{(Polygon) geom});
    } else {
      throw new IllegalArgumentException("La geometría no es polígono: " + geom.getGeometryType());
    }
  }

  private record ProvinciaConGeometria(Provincia provincia, MultiPolygon geometria) {

    public ProvinciaConGeometria(String nombre, MultiPolygon geometria) {
      this(Provincia.conNombre(nombre), geometria);
    }
  }
}
