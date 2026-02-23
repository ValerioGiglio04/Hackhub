package it.hackhub.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO per la registrazione (email, password, nome, cognome, ruolo opzionale).
 */
public class RegistrazioneDTO {

  @NotBlank(message = "L'email è obbligatoria")
  @Email(message = "L'email deve essere valida")
  private String email;

  @NotBlank(message = "La password è obbligatoria")
  @Size(min = 6, message = "La password deve essere di almeno 6 caratteri")
  private String password;

  @NotBlank(message = "Il nome è obbligatorio")
  private String nome;

  @NotBlank(message = "Il cognome è obbligatorio")
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
