package it.hackhub.application.dto.auth;

/**
 * DTO per la registrazione (email, password, nome, cognome, ruolo opzionale).
 * Validazione: campi non vuoti, email valida, password lunghezza minima, nome/cognome formato valido.
 */
public class RegistrazioneDTO {

  private String email;
  private String password;
  private String nome;
  private String cognome;
  /** Ruolo: AUTENTICATO, ORGANIZZATORE, MENTORE, GIUDICE. Se omesso, default AUTENTICATO. */
  private String ruolo;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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

  public String getRuolo() {
    return ruolo;
  }

  public void setRuolo(String ruolo) {
    this.ruolo = ruolo;
  }
}
