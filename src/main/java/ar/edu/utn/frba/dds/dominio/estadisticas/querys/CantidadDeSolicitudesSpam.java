package ar.edu.utn.frba.dds.dominio.estadisticas.querys;

import ar.edu.utn.frba.dds.dominio.estadisticas.EstadisticaResultado;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import ar.edu.utn.frba.dds.dominio.solicitudes.SolicitudHecho;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class CantidadDeSolicitudesSpam extends EstadisticaQuery {

  public CantidadDeSolicitudesSpam() {
    super("CANTIDAD_SOLICITUDES_SPAM");
  }

  @Override
  public EstadisticaResultado generarEstadisticas(
          LocalDateTime fechaInicio, LocalDateTime fechaFin) {

    return new EstadisticaResultado(this.nombreEstadistica, ejecutarQuery(
            fechaInicio, fechaFin), null, fechaInicio, fechaFin);
  }

  @Override
  public String ejecutarQuery(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
    CriteriaBuilder cb = entityManager().getCriteriaBuilder();

    CriteriaQuery<Tuple> cq = cb.createTupleQuery();
    Root<SolicitudHecho> root = cq.from(SolicitudHecho.class);

    // JOIN con Hecho
    Join<SolicitudHecho, Hecho> joinHecho = root.join("hecho");

    // Filtro por fechaCarga
    Predicate fechaPredicate = cb.between(
            joinHecho.get("fechaSuceso"),   // <-- el nombre REAL del atributo en tu entidad Hecho
            fechaInicio,
            fechaFin
    );

    cq.multiselect(
            root.get("id").alias("id"),
            root.get("descripcion").alias("descripcion")
    );

    cq.where(
            cb.and(
                    cb.isTrue(root.get("esSpam")),  // filtro propio de esta query
                    fechaPredicate
            )
    );

    List<Tuple> resultados = entityManager().createQuery(cq).getResultList();

    StringBuilder datos = new StringBuilder("[");
    for (int i = 0; i < resultados.size(); i++) {
      Tuple t = resultados.get(i);
      datos.append("[\"")
              .append(t.get("id"))
              .append("\",\"")
              .append(t.get("descripcion"))
              .append("\"]");
      if (i < resultados.size() - 1) {
        datos.append(",");
      }
    }
    datos.append("]");

    return datos.toString();
  }
}
