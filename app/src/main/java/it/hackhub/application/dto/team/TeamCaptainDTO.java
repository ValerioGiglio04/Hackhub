package it.hackhub.application.dto.team;

/**
 * DTO per nomina nuovo capo team (teamId, nuovoCapoId).
 */
public class TeamCaptainDTO {

  private Long teamId;
  private Long nuovoCapoId;

  public Long getTeamId() {
    return teamId;
  }

  public void setTeamId(Long teamId) {
    this.teamId = teamId;
  }

  public Long getNuovoCapoId() {
    return nuovoCapoId;
  }

  public void setNuovoCapoId(Long nuovoCapoId) {
    this.nuovoCapoId = nuovoCapoId;
  }
}
