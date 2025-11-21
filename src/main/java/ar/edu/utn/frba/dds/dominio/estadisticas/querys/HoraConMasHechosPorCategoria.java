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


public class HoraConMasHechosPorCategoria extends EstadisticaQuery {

  public HoraConMasHechosPorCategoria() {

    super("HORA_CON_MAS_HECHOS_POR_CATEGORIA");
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

    // Extraer la hora desde fechaSuceso
    Expression<Integer> hora = cb.function("HOUR", Integer.class, root.get("fechaSuceso"));

    // Filtro por rango de fechas
    Predicate fechaPredicate = cb.between(
            root.get("fechaSuceso"),
            fechaInicio,
            fechaFin
    );

    // SELECT categoria, hora, COUNT(*)
    cq.multiselect(
                    root.get("categoria").alias("categoria"),
                    hora.alias("hora"),
                    cb.count(root).alias("cantidad")
            )
            .where(fechaPredicate)
            .groupBy(root.get("categoria"), hora)
            .orderBy(
                    cb.asc(root.get("categoria")),
                    cb.desc(cb.count(root)));

    List<Tuple> resultados = entityManager().createQuery(cq).getResultList();

    // Tomar la hora con mayor cantidad por categoría
    Map<Object, Tuple> maxPorCategoria = new LinkedHashMap<>();
    for (Tuple t : resultados) {
      Object categoria = t.get("categoria");
      if (!maxPorCategoria.containsKey(categoria)) {
        maxPorCategoria.put(categoria, t);
      }
    }

    // Construcción del array [["categoria",hora,cantidad],...]
    StringBuilder datos = new StringBuilder("[");
    int i = 0;
    for (Tuple t : maxPorCategoria.values()) {
      if (i++ > 0) {
        datos.append(",");
      }
      datos.append("[\"")
              .append(t.get("categoria"))
              .append("\",")
              .append(t.get("hora"))
              .append(",")
              .append(t.get("cantidad"))
              .append("]");
    }
    datos.append("]");

    return datos.toString();
  }
}