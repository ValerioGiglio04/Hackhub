package it.hackhub.application.dto.utente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UtenteCreateDTO {

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
}
