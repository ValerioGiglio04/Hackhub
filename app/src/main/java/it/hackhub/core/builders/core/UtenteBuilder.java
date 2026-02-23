package it.hackhub.core.builders.core;

import it.hackhub.core.entities.core.Utente;
import java.time.LocalDate;

/**
 * Builder per la classe Utente.
 */
public class UtenteBuilder {

  private String email;
  private String passwordHash;
  private String nome;
  private String cognome;
  private LocalDate dataRegistrazione;
  private Utente.RuoloStaff ruolo = Utente.RuoloStaff.AUTENTICATO;

  public UtenteBuilder() {
    this.dataRegistrazione = LocalDate.now();
  }

  public UtenteBuilder email(String email) {
    this.email = email;
    return this;
  }

  public UtenteBuilder passwordHash(String passwordHash) {
    this.passwordHash = passwordHash;
    return this;
  }

  public UtenteBuilder nome(String nome) {
    this.nome = nome;
    return this;
  }

  public UtenteBuilder cognome(String cognome) {
    this.cognome = cognome;
    return this;
  }

  public UtenteBuilder dataRegistrazione(LocalDate dataRegistrazione) {
    this.dataRegistrazione = dataRegistrazione;
    return this;
  }

  public UtenteBuilder ruolo(Utente.RuoloStaff ruolo) {
    this.ruolo = ruolo;
    return this;
  }

  public Utente build() {
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("L'email è obbligatoria");
    }
    Utente u = new Utente();
    u.setEmail(email);
    u.setPasswordHash(passwordHash != null ? passwordHash : "");
    u.setNome(nome);
    u.setCognome(cognome);
    u.setDataRegistrazione(dataRegistrazione);
    u.setRuolo(ruolo);
    return u;
  }
}
