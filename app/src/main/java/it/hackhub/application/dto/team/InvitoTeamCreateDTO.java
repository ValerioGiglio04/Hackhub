package it.hackhub.application.dto.team;

/**
 * DTO per la creazione di un invito a unirsi a un team (use case Invita Membro team).
 */
public class InvitoTeamCreateDTO {

  private Long teamId;
  private Long utenteInvitatoId;

  public Long getTeamId() {
    return teamId;
  }

  public void setTeamId(Long teamId) {
    this.teamId = teamId;
  }

  public Long getUtenteInvitatoId() {
    return utenteInvitatoId;
  }

  public void setUtenteInvitatoId(Long utenteInvitatoId) {
    this.utenteInvitatoId = utenteInvitatoId;
  }
}
