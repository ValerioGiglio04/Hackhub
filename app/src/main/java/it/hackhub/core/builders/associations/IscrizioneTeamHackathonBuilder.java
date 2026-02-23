package it.hackhub.core.builders.associations;

import it.hackhub.core.entities.associations.IscrizioneTeamHackathon;
import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.Team;
import java.time.LocalDateTime;

public class IscrizioneTeamHackathonBuilder {

  private Team team;
  private Hackathon hackathon;
  private LocalDateTime dataIscrizione;

  public IscrizioneTeamHackathonBuilder team(Team team) {
    this.team = team;
    return this;
  }

  public IscrizioneTeamHackathonBuilder hackathon(Hackathon hackathon) {
    this.hackathon = hackathon;
    return this;
  }

  public IscrizioneTeamHackathonBuilder dataIscrizione(LocalDateTime dataIscrizione) {
    this.dataIscrizione = dataIscrizione;
    return this;
  }

  public IscrizioneTeamHackathon build() {
    if (team == null) {
      throw new IllegalArgumentException("Il team è obbligatorio");
    }
    if (hackathon == null) {
      throw new IllegalArgumentException("L'hackathon è obbligatorio");
    }
    IscrizioneTeamHackathon iscrizione = new IscrizioneTeamHackathon();
    iscrizione.setTeam(team);
    iscrizione.setHackathon(hackathon);
    iscrizione.setDataIscrizione(dataIscrizione != null ? dataIscrizione : LocalDateTime.now());
    return iscrizione;
  }
}
