package ar.edu.utn.frba.dds.dominio.fuentes.proxy.demo;

import ar.edu.utn.frba.dds.dominio.fuentes.cache.FuenteCacheable;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("DEMO")
public class FuenteDemo extends FuenteCacheable {

  @Transient
  private final List<Hecho> hechosDemo = new CopyOnWriteArrayList<>();
  @Transient
  private final ObtenedorDeHechos obtenedorDeHechos;
  private static final Logger logger = LoggerFactory.getLogger(FuenteDemo.class);
  private final String url;


  public FuenteDemo(ObtenedorDeHechos obtenedorDeHechos) {
    super();
    this.url = obtenedorDeHechos.getUrl();
    this.obtenedorDeHechos = obtenedorDeHechos;
  }

  public FuenteDemo(String url) {
    this.url = url;
    this.obtenedorDeHechos = new ObtenedorDeHechos(url, new ConexionHttp());
  }

  public FuenteDemo() {
    //JPA requiere un constructor vacío
    this.url = null;
    this.obtenedorDeHechos = null;
  }

  public void incorporarNuevosHechos(LocalDateTime partiendoDeEstaFecha) {
    List<Hecho> nuevosHechos = obtenedorDeHechos.getNuevosHechos(partiendoDeEstaFecha);
    if (!nuevosHechos.isEmpty()) {
      hechosDemo.addAll(nuevosHechos);
      logger.info("FuenteDemo: Se incorporaron {} nuevos hechos.", nuevosHechos.size());
    } else {
      logger.info("FuenteDemo: No se encontraron nuevos hechos para incorporar.");
    }
  }

  @Override
  public List<Hecho> getHechos() {
    return List.copyOf(hechosDemo);
  }
}
