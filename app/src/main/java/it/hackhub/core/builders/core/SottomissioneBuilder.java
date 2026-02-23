package it.hackhub.core.builders.core;

import it.hackhub.core.entities.core.Sottomissione;
import java.time.LocalDateTime;

/**
 * Builder per la classe Sottomissione (usa teamId e hackathonId).
 */
public class SottomissioneBuilder {

  private Long teamId;
  private Long hackathonId;
  private String linkProgetto;
  private LocalDateTime dataCaricamento;
  private LocalDateTime dataUltimoUpdate;

  public SottomissioneBuilder teamId(Long teamId) {
    this.teamId = teamId;
    return this;
  }

  public SottomissioneBuilder hackathonId(Long hackathonId) {
    this.hackathonId = hackathonId;
    return this;
  }

  public SottomissioneBuilder linkProgetto(String linkProgetto) {
    this.linkProgetto = linkProgetto;
    return this;
  }

  public SottomissioneBuilder dataCaricamento(LocalDateTime dataCaricamento) {
    this.dataCaricamento = dataCaricamento;
    return this;
  }

  public SottomissioneBuilder dataUltimoUpdate(LocalDateTime dataUltimoUpdate) {
    this.dataUltimoUpdate = dataUltimoUpdate;
    return this;
  }

  public Sottomissione build() {
    if (teamId == null) {
      throw new IllegalArgumentException("Il team è obbligatorio");
    }
    if (hackathonId == null) {
      throw new IllegalArgumentException("L'hackathon è obbligatorio");
    }
    LocalDateTime now = LocalDateTime.now();
    Sottomissione s = new Sottomissione();
    s.setTeamId(teamId);
    s.setHackathonId(hackathonId);
    s.setLinkProgetto(linkProgetto != null ? linkProgetto : "");
    s.setDataCaricamento(dataCaricamento != null ? dataCaricamento : now);
    s.setDataUltimoUpdate(dataUltimoUpdate != null ? dataUltimoUpdate : now);
    return s;
  }
}
