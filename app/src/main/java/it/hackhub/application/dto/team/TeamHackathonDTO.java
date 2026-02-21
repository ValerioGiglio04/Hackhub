package it.hackhub.application.dto.team;

/**
 * DTO per iscrizione team ad hackathon (teamId, hackathonId).
 */
public class TeamHackathonDTO {

  private Long teamId;
  private Long hackathonId;

  public Long getTeamId() {
    return teamId;
  }

  public void setTeamId(Long teamId) {
    this.teamId = teamId;
  }

  public Long getHackathonId() {
    return hackathonId;
  }

  public void setHackathonId(Long hackathonId) {
    this.hackathonId = hackathonId;
  }
}
