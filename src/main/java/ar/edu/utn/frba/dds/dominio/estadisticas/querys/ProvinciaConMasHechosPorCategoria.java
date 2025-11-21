package ar.edu.utn.frba.dds.dominio.estadisticas.querys;

import ar.edu.utn.frba.dds.dominio.estadisticas.EstadisticaResultado;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


public class ProvinciaConMasHechosPorCategoria extends EstadisticaQuery {

  public ProvinciaConMasHechosPorCategoria() {
    super("PROVINCIA_CON_MAS_HECHOS_POR_CATEGORIA");
  }

  @Override
  public EstadisticaResultado generarEstadisticas(
          LocalDateTime fechaInicio, LocalDateTime fechaFin) {

    return new EstadisticaResultado(
            this.nombreEstadistica,
            ejecutarQuery(fechaInicio, fechaFin), null, fechaInicio, fechaFin);
  }

  @Override
  public String ejecutarQuery(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
    CriteriaBuilder cb = entityManager().getCriteriaBuilder();

    CriteriaQuery<Tuple> cq = cb.createTupleQuery();
    Root<Hecho> root = cq.from(Hecho.class);

    // Filtro por fecha del Hecho
    Predicate fechaPredicate = cb.between(
            root.get("fechaSuceso"),   // campo de la entidad
            fechaInicio,
            fechaFin
    );
    cq.where(fechaPredicate);

    // SELECT categoria, provincia, COUNT(*) AS cantidad
    Expression<Long> cantidad = cb.count(root);

    cq.multiselect(
                    root.get("categoria").alias("categoria"),
                    root.get("provincia").alias("provincia"),
                    cantidad.alias("cantidad")
            )
            .groupBy(root.get("categoria"), root.get("provincia"))
            .orderBy(cb.asc(root.get("categoria")), cb.desc(cantidad));

    List<Tuple> resultados = entityManager().createQuery(cq).getResultList();

    // Quedarse solo con la provincia de mayor cantidad por categoría
    Map<Object, Tuple> maxPorCategoria = new LinkedHashMap<>();
    for (Tuple t : resultados) {
      Object categoria = t.get("categoria");
      if (!maxPorCategoria.containsKey(categoria)) {
        maxPorCategoria.put(categoria, t);
      }
    }

    // Armar salida [["categoria","provincia",cantidad],...]
    StringBuilder datos = new StringBuilder("[");
    int i = 0;
    for (Tuple t : maxPorCategoria.values()) {
      if (i++ > 0) {
        datos.append(",");
      }
      datos.append("[\"")
              .append(t.get("categoria"))
              .append("\",\"")
              .append(t.get("provincia"))
              .append("\",")
              .append(t.get("cantidad"))
              .append("]");
    }
    datos.append("]");

    return datos.toString();
  }
}
