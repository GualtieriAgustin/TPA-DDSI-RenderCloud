package ar.edu.utn.frba.dds.dominio.colecciones;

import ar.edu.utn.frba.dds.dominio.colecciones.filtros.CriterioDePertenencia;
import ar.edu.utn.frba.dds.dominio.consenso.AlgoritmoDeConsenso;
import ar.edu.utn.frba.dds.dominio.consenso.NivelDeConsenso;
import ar.edu.utn.frba.dds.dominio.fuentes.Fuente;
import ar.edu.utn.frba.dds.dominio.hechos.Hecho;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToOne;

@Entity
public class Coleccion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String titulo;
  private String descripcion;

  @ManyToOne(cascade = CascadeType.ALL)
  private Fuente fuente;

  @OneToOne(cascade = CascadeType.ALL)
  private CriterioDePertenencia criterioPertenecia;

  @Enumerated(EnumType.STRING)
  @Column(name = "algoritmo_de_consenso")
  private AlgoritmoDeConsenso algoritmoDeConsenso;

  @ElementCollection
  @CollectionTable(name = "coleccion_consenso", joinColumns = @JoinColumn(name = "coleccion_id"))
  @MapKeyJoinColumn(name = "hecho_id")
  @Column(name = "nivel_consenso")
  @Enumerated(EnumType.STRING)
  private final Map<Hecho, NivelDeConsenso> resultadosConsenso = new HashMap<>();

  @Enumerated(EnumType.STRING)
  @Column(name = "modo_de_navegacion")
  private ModoDeNavegacion modoDeNavegacion;

  public Coleccion(
      String titulo,
      String descripcion,
      Fuente fuente,
      CriterioDePertenencia criterioPertenecia,
      ModoDeNavegacion modo) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.criterioPertenecia = criterioPertenecia;
    this.modoDeNavegacion = modo;
    this.fuente = fuente;
  }

  protected Coleccion() {

  }

  protected boolean pertenece(Hecho hecho) {
    return criterioPertenecia.cumple(hecho);
  }

  public String getTitulo() {
    return titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public AlgoritmoDeConsenso getAlgoritmoDeConsenso() {
    return algoritmoDeConsenso;
  }

  public void setAlgoritmoDeConsenso(AlgoritmoDeConsenso algoritmo) {
    this.algoritmoDeConsenso = algoritmo;
  }

  // Metodo para que el proceso de ponderación actualice los resultados
  public void actualizarConsenso(Map<Hecho, NivelDeConsenso> nuevosResultados) {
    this.resultadosConsenso.clear();
    this.resultadosConsenso.putAll(nuevosResultados);
  }

  private boolean cumpleConsenso(Hecho hecho) {
    NivelDeConsenso valorPorDefecto = this.algoritmoDeConsenso != null
        // Si hay algoritmo, por defecto un hecho no evaluado no cumple.
        ? NivelDeConsenso.NO_CONSENSUADO
        // Si no hay algoritmo, por defecto todos se consideran consensuados.
        : NivelDeConsenso.CONSENSUADO;

    NivelDeConsenso resultadoConsenso =
        this.resultadosConsenso.getOrDefault(hecho, valorPorDefecto);

    return resultadoConsenso == NivelDeConsenso.CONSENSUADO;
  }

  public void setModoDeNavegacion(ModoDeNavegacion modoDeNavegacion) {
    this.modoDeNavegacion = modoDeNavegacion;
  }

  protected boolean cumpleModoDeNavegacion(Hecho hecho) {
    if (this.modoDeNavegacion == ModoDeNavegacion.IRRESTRICTO) {
      return true; // En modo irrestricto, todos los hechos cumplen.
    }
    // En modo CURADO, solo pasan los que cumplen el consenso.
    return this.cumpleConsenso(hecho);
  }

  public ModoDeNavegacion getModoDeNavegacion() {
    return this.modoDeNavegacion;
  }

  public Long getId() {
    return id;
  }

  public Fuente obtenerFuente() {
    return fuente;
  }

  public CriterioDePertenencia obtenerCriterio() {
    return criterioPertenecia;
  }
}
