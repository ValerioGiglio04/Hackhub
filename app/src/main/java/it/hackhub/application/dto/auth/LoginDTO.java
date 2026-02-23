package it.hackhub.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO per la richiesta di login (email e password).
 */
public class LoginDTO {

  @NotBlank(message = "L'email è obbligatoria")
  @Email(message = "L'email deve essere valida")
  private String email;

  @NotBlank(message = "La password è obbligatoria")
  private String password;

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
}
