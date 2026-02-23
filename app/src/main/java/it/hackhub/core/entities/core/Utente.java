package it.hackhub.core.entities.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entità Utente (capo/membro team, staff hackathon).
 */
@Entity
@Table(name = "Utenti")
public class Utente {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String email;
  @JsonIgnore
  @Column(name = "password_hash")
  private String passwordHash;
  private String nome;
  private String cognome;
  @Column(name = "data_registrazione")
  private LocalDate dataRegistrazione;
  @Enumerated(EnumType.STRING)
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

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public LocalDate getDataRegistrazione() {
    return dataRegistrazione;
  }

  public void setDataRegistrazione(LocalDate dataRegistrazione) {
    this.dataRegistrazione = dataRegistrazione;
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
