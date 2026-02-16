package it.hackhub.application.dto.hackathon;

/**
 * DTO per proclamare il team vincitore (Use case: Proclama vincitore).
 */
public class HackathonWinnerDTO {

  private Long hackathonId;
  private Long teamId;

  public Long getHackathonId() { return hackathonId; }
  public void setHackathonId(Long hackathonId) { this.hackathonId = hackathonId; }
  public Long getTeamId() { return teamId; }
  public void setTeamId(Long teamId) { this.teamId = teamId; }
}
