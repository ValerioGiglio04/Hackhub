package it.hackhub.application.dto.utente;

import it.hackhub.core.entities.core.Utente;
import java.time.LocalDate;

/**
 * DTO per la risposta con i dati dell'utente (profilo, dopo registrazione).
 */
public class UtenteResponseDTO {

  private Long id;
  private String nome;
  private String cognome;
  private String email;
  private LocalDate dataRegistrazione;
  private Utente.RuoloStaff ruolo;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LocalDate getDataRegistrazione() {
    return dataRegistrazione;
  }

  public void setDataRegistrazione(LocalDate dataRegistrazione) {
    this.dataRegistrazione = dataRegistrazione;
  }

  public Utente.RuoloStaff getRuolo() {
    return ruolo;
  }

  public void setRuolo(Utente.RuoloStaff ruolo) {
    this.ruolo = ruolo;
  }
}
