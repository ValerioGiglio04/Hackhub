package it.hackhub.application.dto.utente;

import it.hackhub.application.dto.hackathon.HackathonSummaryDTO;
import it.hackhub.application.dto.team.TeamSummaryDTO;
import it.hackhub.core.entities.core.Utente;
import java.util.List;

/**
 * DTO con i ruoli completi dell'utente (ruolo staff + appartenenza team).
 */
public class UtenteRuoliResponseDTO {

  private Long utenteId;
  private String email;
  private String nome;
  private String cognome;
  private RuoloStaffDTO ruoloStaff;
  private Boolean isInTeam;
  private TeamSummaryDTO team;
  private Boolean isTeamLeader;

  public Long getUtenteId() {
    return utenteId;
  }

  public void setUtenteId(Long utenteId) {
    this.utenteId = utenteId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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

  public RuoloStaffDTO getRuoloStaff() {
    return ruoloStaff;
  }

  public void setRuoloStaff(RuoloStaffDTO ruoloStaff) {
    this.ruoloStaff = ruoloStaff;
  }

  public Boolean getIsInTeam() {
    return isInTeam;
  }

  public void setIsInTeam(Boolean isInTeam) {
    this.isInTeam = isInTeam;
  }

  public TeamSummaryDTO getTeam() {
    return team;
  }

  public void setTeam(TeamSummaryDTO team) {
    this.team = team;
  }

  public Boolean getIsTeamLeader() {
    return isTeamLeader;
  }

  public void setIsTeamLeader(Boolean isTeamLeader) {
    this.isTeamLeader = isTeamLeader;
  }

  /**
   * Dettagli del ruolo staff dell'utente.
   */
  public static class RuoloStaffDTO {

    private Utente.RuoloStaff ruolo;
    private List<HackathonSummaryDTO> hackathons;

    public Utente.RuoloStaff getRuolo() {
      return ruolo;
    }

    public void setRuolo(Utente.RuoloStaff ruolo) {
      this.ruolo = ruolo;
    }

    public List<HackathonSummaryDTO> getHackathons() {
      return hackathons;
    }

    public void setHackathons(List<HackathonSummaryDTO> hackathons) {
      this.hackathons = hackathons;
    }
  }
}
