package ar.edu.utn.frba.dds.dominio.hechos;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public enum Provincia {
  BUENOS_AIRES("Buenos Aires"),
  CABA("Ciudad Autónoma de Buenos Aires"),
  CATAMARCA("Catamarca"),
  CHACO("Chaco"),
  CHUBUT("Chubut"),
  CORDOBA("Córdoba"),
  CORRIENTES("Corrientes"),
  ENTRE_RIOS("Entre Ríos"),
  FORMOSA("Formosa"),
  JUJUY("Jujuy"),
  LA_PAMPA("La Pampa"),
  LA_RIOJA("La Rioja"),
  MENDOZA("Mendoza"),
  MISIONES("Misiones"),
  NEUQUEN("Neuquén"),
  RIO_NEGRO("Río Negro"),
  SALTA("Salta"),
  SAN_JUAN("San Juan"),
  SAN_LUIS("San Luis"),
  SANTA_CRUZ("Santa Cruz"),
  SANTA_FE("Santa Fe"),
  SANTIAGO_DEL_ESTERO("Santiago del Estero"),
  TIERRA_DEL_FUEGO("Tierra del Fuego, Antártida e Islas del Atlántico Sur"),
  TUCUMAN("Tucumán"),
  PROVINCIA_DESCONOCIDA("Provincia Desconocida");

  private final String nombreGeoJson;

  Provincia(String nombreGeoJson) {
    this.nombreGeoJson = nombreGeoJson;
  }

  public static Provincia conNombre(String nombre) {
    if (nombre == null) {
      return PROVINCIA_DESCONOCIDA;
    }
    for (Provincia p : values()) {
      if (p.nombreGeoJson.equalsIgnoreCase(nombre.trim())) {
        return p;
      }
    }
    return PROVINCIA_DESCONOCIDA;
  }

  public String getNombreGeoJson() {
    return nombreGeoJson;
  }

  public static List<Map<String, String>> todas() {
    return Arrays.stream(Provincia.values()).map(Provincia::toMap).toList();
  }

  public Map<String, String> toMap() {
    return Map.of("value", this.toString(), "nombre", this.nombreGeoJson);
  }

}
