package it.hackhub.application.dto.valutazioni;

import it.hackhub.application.dto.hackathon.HackathonSummaryDTO;
import it.hackhub.application.dto.team.TeamSummaryDTO;

/**
 * DTO di riepilogo per riferimenti a sottomissioni nelle risposte.
 */
public class SottomissioneSummaryDTO {

  private Long id;
  private TeamSummaryDTO team;
  private HackathonSummaryDTO hackathon;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public TeamSummaryDTO getTeam() {
    return team;
  }

  public void setTeam(TeamSummaryDTO team) {
    this.team = team;
  }

  public HackathonSummaryDTO getHackathon() {
    return hackathon;
  }

  public void setHackathon(HackathonSummaryDTO hackathon) {
    this.hackathon = hackathon;
  }
}
