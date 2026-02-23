package it.hackhub.application.dto.team;

/**
 * DTO di riepilogo per riferimenti a team nelle risposte (es. team vincitore).
 */
public class TeamSummaryDTO {

  private Long id;
  private String nome;

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
}
