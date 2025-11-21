package ar.edu.utn.frba.dds.dominio.archivos;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.util.List;

public interface LectorArchivo {

  List<Hecho> leerHechos();

  List<Hecho> leerHechos(int pagina, int cantidad);

  String getFileName();
}
