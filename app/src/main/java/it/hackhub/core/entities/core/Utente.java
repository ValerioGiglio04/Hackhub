package it.hackhub.core.entities.core;

import java.util.Objects;

/**
 * Entità Utente (capo/membro team, staff hackathon).
 */
public class Utente {

  private Long id;
  private String email;
  private String nome;
  private String cognome;
  private RuoloStaff ruolo = RuoloStaff.AUTENTICATO;

  public enum RuoloStaff {
    AUTENTICATO,
    ORGANIZZATORE,
    MENTORE,
    GIUDICE,
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getCognome() {
    return cognome;
  }

  public void setCognome(String cognome) {
    this.cognome = cognome;
  }

  public RuoloStaff getRuolo() {
    return ruolo;
  }

  public void setRuolo(RuoloStaff ruolo) {
    this.ruolo = ruolo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Utente utente = (Utente) o;
    return Objects.equals(id, utente.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
