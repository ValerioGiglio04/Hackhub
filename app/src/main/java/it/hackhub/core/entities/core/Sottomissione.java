package it.hackhub.core.entities.core;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entità Sottomissione (conclusione fase svolgimento, valutazioni).
 */
public class Sottomissione {

  private Long id;
  private Long teamId;
  private Long hackathonId;
  private String linkProgetto;
  private LocalDateTime dataCaricamento;
  private LocalDateTime dataUltimoUpdate;

  public Sottomissione() {}

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

  public String getLinkProgetto() {
    return linkProgetto;
  }

  public void setLinkProgetto(String linkProgetto) {
    this.linkProgetto = linkProgetto;
  }

  public LocalDateTime getDataCaricamento() {
    return dataCaricamento;
  }

  public void setDataCaricamento(LocalDateTime dataCaricamento) {
    this.dataCaricamento = dataCaricamento;
  }

  public LocalDateTime getDataUltimoUpdate() {
    return dataUltimoUpdate;
  }

  public void setDataUltimoUpdate(LocalDateTime dataUltimoUpdate) {
    this.dataUltimoUpdate = dataUltimoUpdate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Sottomissione that = (Sottomissione) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
