package ar.edu.utn.frba.dds.persistencia.dialect;

import org.hibernate.dialect.PostgreSQL10Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.BooleanType;
import org.hibernate.type.FloatType;

public class PostgreFullTextSearchDialect extends PostgreSQL10Dialect {

  public PostgreFullTextSearchDialect() {
    super();
    registerFunction(
        "fts",
        new SQLFunctionTemplate(
            BooleanType.INSTANCE, "to_tsvector('spanish', ?1) @@ plainto_tsquery('spanish', ?2)"));

    registerFunction(
        "similarity",
        new SQLFunctionTemplate(
            FloatType.INSTANCE,
            "similarity(unaccent(lower(?1)), unaccent(lower(?2)))"
        )
    );

    registerFunction(
        "text_contains",
        new SQLFunctionTemplate(
            BooleanType.INSTANCE,
            "immutable_unaccent(lower(?1)) ILIKE immutable_unaccent(lower(?2))"
        )
    );

  }
}
