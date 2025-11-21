package ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.fuentes.Fuente;
import ar.edu.utn.frba.dds.dominio.fuentes.proxy.instancias.clients.contratos.FiltroHechoRequest;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.util.List;
import javax.persistence.Entity;

@Entity
public abstract class ServicioMetamapa extends Fuente {

  public List<Hecho> getHechos() {
    return getHechos(new CriterioDePertenencia());
  }

  public abstract List<Hecho> getHechos(FiltroHechoRequest filtro);

  public List<Hecho> getHechos(CriterioDePertenencia criterioDePertenencia) {
    return getHechos(FiltroHechoRequest.crearFrom(criterioDePertenencia));
  }

}
