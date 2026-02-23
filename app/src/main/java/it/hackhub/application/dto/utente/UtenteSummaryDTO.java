package it.hackhub.application.dto.utente;

/**
 * DTO di riepilogo per riferimenti a utenti nelle risposte (es. capo team).
 */
public class UtenteSummaryDTO {

  private Long id;
  private String nome;
  private String cognome;

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
}
