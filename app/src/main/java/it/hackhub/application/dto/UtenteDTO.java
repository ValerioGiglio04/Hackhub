package it.hackhub.application.dto;

/**
 * DTO per i dati di un utente (capo/membro team).
 */
public class UtenteDTO {

  private Long id;
  private String nome;
  private String cognome;
  private String email;

  public UtenteDTO() {}

  public UtenteDTO(Long id, String nome, String cognome, String email) {
    this.id = id;
    this.nome = nome;
    this.cognome = cognome;
    this.email = email;
  }

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
}
