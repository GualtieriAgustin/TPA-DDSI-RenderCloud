package ar.edu.utn.frba.dds.dominio.usuarios;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String nombre;
  private String password;
  private String  email;
  private String  username;
  private Boolean isAdmin;

  @Column(name = "esta_registrado")
  private boolean estaRegistrado;


  protected Usuario() {}

  public Usuario(String nombre, String password, String email, String username) {
    this.nombre = nombre;
    this.password = password;
    this.email = email;
    this.username = username;
    this.estaRegistrado = true;
    this.isAdmin = false;
  }

  public Usuario(String nombre) {
    this.nombre = nombre;
    this.estaRegistrado = false;
  }

  public Usuario(String nombre, boolean estaRegistrado) {
    this.nombre = nombre;
    this.estaRegistrado = estaRegistrado;
  }

  public Usuario(String nombre, boolean estaRegistrado, Long id) {
    this(nombre, estaRegistrado);
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public boolean estaRegistrado() {
    return estaRegistrado;
  }




  public Boolean matchPassword(String password) {
    Argon2 argon2 = Argon2Factory.create();
    return argon2.verify(this.password, password.toCharArray());
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setIsAdmin(boolean isAdmin) {
    this.isAdmin = isAdmin;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getEmail() {
    return email;
  }

  public String getUsername() {
    return username;
  }

  public Boolean getAdmin() {
    return isAdmin;
  }

}
