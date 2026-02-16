package it.hackhub.application.dto.supporto;

public class RichiestaSupportoCreateDTO {

  private Long teamId;
  private Long hackathonId;
  private String descrizione;
  private String stato;

  public Long getTeamId() { return teamId; }
  public void setTeamId(Long teamId) { this.teamId = teamId; }
  public Long getHackathonId() { return hackathonId; }
  public void setHackathonId(Long hackathonId) { this.hackathonId = hackathonId; }
  public String getDescrizione() { return descrizione; }
  public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
  public String getStato() { return stato; }
  public void setStato(String stato) { this.stato = stato; }
}
