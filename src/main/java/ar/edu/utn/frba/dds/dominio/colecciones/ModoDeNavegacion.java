package ar.edu.utn.frba.dds.dominio.colecciones;

/**
 * Define los modos en que se pueden navegar los hechos de una colección.
 */
public enum ModoDeNavegacion {
  /**
   * Muestra todos los hechos que pertenecen a la colección, sin importar su consenso.
   */
  IRRESTRICTO,

  /**
   * Muestra únicamente los hechos que cumplen con el algoritmo de consenso configurado.
   */
  CURADO
}