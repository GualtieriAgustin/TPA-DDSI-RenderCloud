package ar.edu.utn.frba.dds.utils.builders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ar.edu.utn.frba.dds.dominio.colecciones.Coleccion;
import ar.edu.utn.frba.dds.dominio.colecciones.ModoDeNavegacion;
import ar.edu.utn.frba.dds.dominio.colecciones.builders.ColeccionBuilder;
import ar.edu.utn.frba.dds.dominio.colecciones.filtros.FiltroCategoria;
import ar.edu.utn.frba.dds.dominio.fuentes.FuenteDinamica;
import ar.edu.utn.frba.dds.persistencia.hecho.RepositorioDeHechos;
import org.junit.jupiter.api.Test;

class ColeccionBuilderTest {

  @Test
  void build_construyeUnaColeccionConDatosValidos() {
    Coleccion coleccion = new ColeccionBuilder()
        .conTitulo("Título Test")
        .conDescripcion("Descripción Test")
        .conFuente(new FuenteDinamica(new RepositorioDeHechos()))
        .conFiltro(new FiltroCategoria("Accidente"))
        .build();

    assertNotNull(coleccion);
    assertEquals("Título Test", coleccion.getTitulo());
    assertEquals("Descripción Test", coleccion.getDescripcion());
  }

  /**
   * Verifica que si no se especifica un modo de navegación, el builder
   * asigna IRRESTRICTO por defecto.
   */
  @Test
  void build_siNoSeEspecificaModo_usaModoIrrestrictoPorDefecto() {
    Coleccion coleccion = new ColeccionBuilder()
        .conTitulo("Colección con modo por defecto")
        .conFuente(new FuenteDinamica(new RepositorioDeHechos()))
        .build();

    // Assert: Verificamos que el modo asignado es IRRESTRICTO
    assertNotNull(coleccion);
    assertEquals(ModoDeNavegacion.IRRESTRICTO, coleccion.getModoDeNavegacion());
  }

  /**
   * Verifica que se puede configurar explícitamente el modo de navegación
   * a CURADO y la colección se construye con ese valor.
   */
  @Test
  void build_cuandoSeEspecificaModoCurado_loConfiguraCorrectamente() {
    Coleccion coleccion = new ColeccionBuilder()
        .conTitulo("Colección con modo explícito")
        .conModoDeNavegacion(ModoDeNavegacion.CURADO)
        .conFuente(new FuenteDinamica(new RepositorioDeHechos()))
        .build();

    // Assert: Verificamos que el modo asignado es CURADO
    assertNotNull(coleccion);
    assertEquals(ModoDeNavegacion.CURADO, coleccion.getModoDeNavegacion());
  }
}
