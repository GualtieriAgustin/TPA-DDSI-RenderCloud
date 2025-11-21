package ar.edu.utn.frba.dds.utils.factories;

import ar.edu.utn.frba.dds.dominio.fuentes.FuenteEstatica;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.archivos.LectorArchivo;
import java.util.List;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class FuenteMockFactory {
  public FuenteEstatica create(List<Hecho> hechos) {
    LectorArchivo lectorMock = Mockito.mock(LectorArchivo.class);
    when(lectorMock.leerHechos()).thenReturn(hechos);
    return new FuenteEstatica(lectorMock);
  }
}
