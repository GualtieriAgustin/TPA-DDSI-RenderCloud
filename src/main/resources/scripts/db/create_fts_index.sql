CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS unaccent;

CREATE OR REPLACE FUNCTION immutable_unaccent(text)
    RETURNS text
    LANGUAGE sql
    IMMUTABLE PARALLEL SAFE STRICT AS
$func$
SELECT public.unaccent($1)
$func$;

CREATE OR REPLACE FUNCTION immutable_concat_ws(sep text, VARIADIC args text[])
    RETURNS text
    LANGUAGE sql
    IMMUTABLE
AS
$$
SELECT concat_ws(sep, VARIADIC args);
$$;


ALTER TABLE hecho
    ADD COLUMN search_vector tsvector
        GENERATED ALWAYS AS (
            setweight(to_tsvector('spanish', coalesce(titulo, '')), 'A') ||
            setweight(to_tsvector('spanish', coalesce(descripcion, '')), 'B') ||
            setweight(to_tsvector('spanish', coalesce(categoria, '')), 'C')
            ) STORED;

CREATE INDEX idx_hecho_fts ON hecho USING GIN (search_vector);
CREATE INDEX idx_hecho_trgm ON hecho USING GIN (immutable_unaccent(lower(immutable_concat_ws(' ', titulo, descripcion, categoria)))
                                                gin_trgm_ops);