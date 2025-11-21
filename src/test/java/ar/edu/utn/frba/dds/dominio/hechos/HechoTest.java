package ar.edu.utn.frba.dds.dominio.hechos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ar.edu.utn.frba.dds.dominio.hechos.exceptions.HechoNoCreadoPorUsuarioException;
import ar.edu.utn.frba.dds.dominio.fuentes.Origen;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.Multimedia;
import ar.edu.utn.frba.dds.dominio.hechos.multimedia.TipoMultimedia;
import ar.edu.utn.frba.dds.dominio.usuarios.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class HechoTest {


  @Test
  public void constructor_DebeSetearCorrectamenteLasPropiedades() {
    // Arrange
    String titulo = "Incendio";
    String descripcion = "Incendio en un edificio";
    String categoria = "Accidente";
    Ubicacion ubicacion = new Ubicacion(10.0, 20.0);
    LocalDateTime fechaSuceso = LocalDateTime.of(2023, 10, 1, 14, 30);
    Origen origen = Origen.DATASET;
    LocalDateTime fechaCarga = LocalDateTime.of(2025, 4, 27, 10, 0);
    Multimedia multimedia = new Multimedia("url", TipoMultimedia.IMAGEN, "descripcion");

    // Act
    Hecho hecho = new Hecho(
        titulo,
        descripcion,
        categoria,
        ubicacion,
        fechaSuceso,
        origen,
        fechaCarga,
        List.of(multimedia),
        Provincia.PROVINCIA_DESCONOCIDA
    );

    // Assert
    assertEquals(titulo, hecho.getTitulo());
    assertEquals(descripcion, hecho.getDescripcion());
    assertEquals(categoria, hecho.getCategoria());
    assertEquals(ubicacion, hecho.getUbicacion());
    assertEquals(fechaSuceso, hecho.getFechaSuceso());
    assertEquals(origen, hecho.getOrigen());
    assertEquals(fechaCarga, hecho.getFechaCarga());
    assertEquals(multimedia, hecho.getMultimedias().get(0));
  }


  @Test
  void Hecho_constructor_CuandoRecibeUsuario_DebeSetearUsuarioCorrectamente() {
    // Arrange
    String titulo = "Incendio";
    String descripcion = "Incendio en un edificio";
    String categoria = "Accidente";
    Ubicacion ubicacion = new Ubicacion(10.0, 20.0);
    LocalDateTime fechaSuceso = LocalDateTime.of(2023, 10, 1, 14, 30);
    LocalDateTime fechaCarga = LocalDateTime.of(2025, 4, 27, 10, 0);
    Multimedia multimedia = new Multimedia("url", TipoMultimedia.IMAGEN, "descripcion");
    Usuario usuario = new Usuario("Ciro");

    // Act
    Hecho hecho = new Hecho(
        titulo,
        descripcion,
        categoria,
        ubicacion,
        fechaSuceso,
        fechaCarga,
        List.of(multimedia),
        usuario,
        Provincia.PROVINCIA_DESCONOCIDA
    );

    // Assert
    assertEquals(titulo, hecho.getTitulo());
    assertEquals(descripcion, hecho.getDescripcion());
    assertEquals(categoria, hecho.getCategoria());
    assertEquals(ubicacion, hecho.getUbicacion());
    assertEquals(fechaSuceso, hecho.getFechaSuceso());
    assertEquals(Origen.CONTRIBUYENTE, hecho.getOrigen());
    assertEquals(fechaCarga, hecho.getFechaCarga());
    assertEquals(multimedia, hecho.getMultimedias().get(0));
  }

  @Test
  public void getLatitudYLongitud_retornaLatitudYLongitudCorrectas() {
    // Arrange
    Double latitud = 10.0;
    Double longitud = 20.0;
    Ubicacion ubicacion = new Ubicacion(latitud, longitud);
    Hecho hecho = new Hecho(
        "titulo",
        "descripcion",
        "categoria",
        ubicacion,
        LocalDateTime.now(),
        Origen.DATASET,
        LocalDateTime.now(),
        Provincia.PROVINCIA_DESCONOCIDA
    );

    // Act
    Double latitudObtenida = hecho.getLatitud();
    Double longitudObtenida = hecho.getLongitud();

    // Assert
    assertEquals(latitud, latitudObtenida);
    assertEquals(longitud, longitudObtenida);
  }

  @Test
  public void setters_ConArgumentosInvalidos_DebeLanzarExcepcion() {
    // Arrange
    Hecho hecho = new Hecho("Titulo", "Desc", "Cat", new Ubicacion(1.0, 1.0), LocalDateTime.now(), Origen.DATASET, LocalDateTime.now(), Provincia.BUENOS_AIRES);

    // Act & Assert
    // Se espera que los setters validen que los campos esenciales no sean nulos o vacíos.
    // Usamos assertThrows para verificar que se lanza la excepción esperada.

    // Test con título nulo o en blanco
    assertThrows(IllegalArgumentException.class, () -> hecho.setTitulo(null), "El título no puede ser nulo.");
    assertThrows(IllegalArgumentException.class, () -> hecho.setTitulo("  "), "El título no puede estar en blanco.");

    // Test con descripción nula o en blanco
    assertThrows(IllegalArgumentException.class, () -> hecho.setDescripcion(null), "La descripción no puede ser nula.");
    assertThrows(IllegalArgumentException.class, () -> hecho.setDescripcion(""), "La descripción no puede estar en blanco.");
  }

  @Test
  public void constructorParaContribuyente_ConUsuarioNulo_DebeLanzarExcepcion() {
    // Arrange
    String titulo = "Aporte de usuario";
    String descripcion = "Descripción del aporte";
    String categoria = "General";
    Ubicacion ubicacion = new Ubicacion(0.0, 0.0);
    LocalDateTime fechaSuceso = LocalDateTime.now();
    LocalDateTime fechaCarga = LocalDateTime.now();
    Usuario usuarioNulo = null; // La condición que queremos probar

    // Act & Assert
    // Verificamos que al llamar al constructor para contribuyentes con un usuario nulo,
    // se lance la excepción HechoNoCreadoPorUsuarioException.
    assertThrows(HechoNoCreadoPorUsuarioException.class, () -> {
      new Hecho(
          titulo,
          descripcion,
          categoria,
          ubicacion,
          fechaSuceso,
          fechaCarga,
          List.of(),
          usuarioNulo,
          Provincia.BUENOS_AIRES);
    }, "Debería lanzar una excepción si se intenta crear un hecho de contribuyente sin usuario.");
  }
}
