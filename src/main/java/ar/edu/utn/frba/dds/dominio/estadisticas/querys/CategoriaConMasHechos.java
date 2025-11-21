package ar.edu.utn.frba.dds.dominio.estadisticas.querys;

import ar.edu.utn.frba.dds.dominio.estadisticas.EstadisticaResultado;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


public class CategoriaConMasHechos extends EstadisticaQuery {

  public CategoriaConMasHechos() {
    super("CATEGORIA_CON_MAS_HECHOS");
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

    // Filtro por fechaSuceso
    Predicate fechaPredicate = cb.between(
            root.get("fechaSuceso"),
            fechaInicio,
            fechaFin
    );

    cq.multiselect(
                    root.get("categoria").alias("categoria"),
                    cb.count(root).alias("cantidad")
            )
            .where(fechaPredicate)
            .groupBy(root.get("categoria"))
            .orderBy(cb.desc(cb.count(root)));

    List<Tuple> resultados = entityManager().createQuery(cq).getResultList();

    // Construcción del JSON-like
    StringBuilder datos = new StringBuilder("[");
    for (int i = 0; i < resultados.size(); i++) {
      Tuple t = resultados.get(i);
      datos.append("[\"")
              .append(t.get("categoria"))
              .append("\",")
              .append(t.get("cantidad"))
              .append("]");
      if (i < resultados.size() - 1) {
        datos.append(",");
      }
    }
    datos.append("]");

    return datos.toString();
  }
}