package it.hackhub.application.dto.supporto;

public class SegnalazioneViolazioneCreateDTO {

  private Long teamSegnalatoId;
  private Long mentoreSegnalanteUtenteId;
  private Long hackathonId;
  private String descrizione;

  public Long getTeamSegnalatoId() { return teamSegnalatoId; }
  public void setTeamSegnalatoId(Long teamSegnalatoId) { this.teamSegnalatoId = teamSegnalatoId; }
  public Long getMentoreSegnalanteUtenteId() { return mentoreSegnalanteUtenteId; }
  public void setMentoreSegnalanteUtenteId(Long mentoreSegnalanteUtenteId) { this.mentoreSegnalanteUtenteId = mentoreSegnalanteUtenteId; }
  public Long getHackathonId() { return hackathonId; }
  public void setHackathonId(Long hackathonId) { this.hackathonId = hackathonId; }
  public String getDescrizione() { return descrizione; }
  public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
}
