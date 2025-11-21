package ar.edu.utn.frba.dds.utils.archivos;

import ar.edu.utn.frba.dds.dominio.archivos.LectorArchivo;
import ar.edu.utn.frba.dds.dominio.archivos.LectorArchivoCsv;
import ar.edu.utn.frba.dds.dominio.fuentes.Fuente;
import ar.edu.utn.frba.dds.dominio.fuentes.FuenteEstatica;

public class ImportarHechos {
  public static Fuente deArchivo(String path) {
    LectorArchivo lector = new LectorArchivoCsv(path, null);
    Fuente fuente = new FuenteEstatica(lector);
    return fuente;
  }
}
