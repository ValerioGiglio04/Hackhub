package it.hackhub.core.entities.associations;

import java.time.LocalDateTime;

/**
 * Iscrizione di un team a un hackathon.
 */
public class IscrizioneTeamHackathon {

  private Long id;
  private Long teamId;
  private Long hackathonId;
  private LocalDateTime dataIscrizione;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public LocalDateTime getDataIscrizione() {
    return dataIscrizione;
  }

  public void setDataIscrizione(LocalDateTime dataIscrizione) {
    this.dataIscrizione = dataIscrizione;
  }
}
