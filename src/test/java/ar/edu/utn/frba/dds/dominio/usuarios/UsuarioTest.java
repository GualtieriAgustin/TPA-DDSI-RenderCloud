package ar.edu.utn.frba.dds.dominio.usuarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class UsuarioTest {

  @Test
  void Usuario_CuandoSoloNombre_DebeCrearNoRegistrado() {
    Usuario usuario = new Usuario("Ciro");

    assertEquals("Ciro", usuario.getNombre());
    assertFalse(usuario.estaRegistrado());
  }

  @Test
  void Usuario_CuandoNombreYRegistradoTrue_DebeCrearRegistrado() {
    Usuario usuario = new Usuario("Ciro", true);

    assertEquals("Ciro", usuario.getNombre());
    assertTrue(usuario.estaRegistrado());
  }
}
