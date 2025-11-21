package ar.edu.utn.frba.dds.persistencia.hecho;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class RepositorioDeHechos implements WithSimplePersistenceUnit, RepositorioHechoDinamico {
  @Override
  public Hecho crear(Hecho hecho) {
    entityManager().persist(hecho);
    return hecho;
  }

  @Override
  public List<Hecho> consultarTodos() {
    return entityManager()
        .createQuery("from Hecho", Hecho.class)
        .getResultList();
  }

  @Override
  public List<Hecho> consultarPorCriterio(CriterioDePertenencia criterio) {
    CriteriaBuilder cb = entityManager().getCriteriaBuilder();
    CriteriaQuery<Hecho> query = cb.createQuery(Hecho.class);
    Root<Hecho> hechoRoot = query.from(Hecho.class);

    List<Predicate> predicados = criterio.predicados(cb, hechoRoot);

    query.where(cb.and(predicados.toArray(new Predicate[0])));

    return entityManager().createQuery(query).getResultList();
  }

  @Override
  public Hecho consultarPorId(Long id) {
    // Usamos find() que es el método ideal para buscar por PK. Devuelve null si no lo encuentra.
    return entityManager().find(Hecho.class, id);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Hecho> consultarPorUsuario(Long idUsuario) {
    return entityManager()
        .createQuery("from Hecho h where h.usuario.id = :idUsuario")
        .setParameter("idUsuario", idUsuario)
        .getResultList();
  }

  @Override
  public void actualizar(Hecho hecho) {
    //TODO
  }


}
