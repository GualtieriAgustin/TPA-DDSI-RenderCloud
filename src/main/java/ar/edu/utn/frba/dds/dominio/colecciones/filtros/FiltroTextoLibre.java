package ar.edu.utn.frba.dds.dominio.colecciones.filtros;

import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Entity
@DiscriminatorValue("TEXTO_LIBRE")
public class FiltroTextoLibre extends FiltroHecho {
  // Umbral para la búsqueda por similitud. Un valor más alto es más estricto.
  private static final float SIMILARITY_THRESHOLD = 0.20F;

  @Column(length = 1765)
  private String texto;

  public FiltroTextoLibre(String texto) {
    this.texto = texto;
  }

  protected FiltroTextoLibre() {
  }

  @Override
  public boolean cumple(Hecho hecho) {
    String busqueda = texto.toLowerCase().trim();
    return hecho.getTitulo().toLowerCase().contains(busqueda)
        || hecho.getDescripcion().toLowerCase().contains(busqueda)
        || hecho.getCategoria().toLowerCase().contains(busqueda);
  }

  @Override
  public Predicate predicado(CriteriaBuilder cb, Root<Hecho> root) {
    Expression<String> documento = cb.function(
        "immutable_concat_ws",
        String.class,
        cb.literal(" "),
        root.get("titulo"),
        root.get("descripcion"),
        root.get("categoria")
    );

    // 1. Búsqueda de texto completo (FTS)
    Expression<Boolean> ftsMatchExpression =
        cb.function("fts", Boolean.class, cb.literal("hecho0_.search_vector"), cb.literal(texto));
    Predicate ftsMatch = cb.isTrue(ftsMatchExpression);

    // 2. Búsqueda por similitud (fuzzy search)
    Expression<Float> fuzzyScore =
        cb.function("similarity", Float.class, documento, cb.literal(texto));
    Predicate fuzzyMatch = cb.greaterThan(fuzzyScore, cb.literal(SIMILARITY_THRESHOLD));

    // 2.1 Fuzzy por título
    Expression<Float> fuzzyScoreTitle =
        cb.function("similarity", Float.class, root.get("titulo"), cb.literal(texto));
    Predicate fuzzyMatchTitle = cb.greaterThan(fuzzyScoreTitle, cb.literal(SIMILARITY_THRESHOLD));

    // 3. ILIKE
    Expression<Boolean> containsMatchExpression =
        cb.function("text_contains", Boolean.class,
            documento,
            cb.literal("%" + texto + "%")
        );
    Predicate containsMatch = cb.isTrue(containsMatchExpression);

    return cb.or(ftsMatch, containsMatch, fuzzyMatch, fuzzyMatchTitle);
  }
}
