package it.hackhub.core.entities.support;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entità Richiesta di supporto
 */
public class RichiestaSupporto {

  private Long id;
  private Long teamId;
  private Long hackathonId;
  private String descrizione;
  private LocalDateTime dataRichiesta;
  private String stato;
  private String linkCallProposto;

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

  public String getDescrizione() {
    return descrizione;
  }

  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  public LocalDateTime getDataRichiesta() {
    return dataRichiesta;
  }

  public void setDataRichiesta(LocalDateTime dataRichiesta) {
    this.dataRichiesta = dataRichiesta;
  }

  public String getStato() {
    return stato;
  }

  public void setStato(String stato) {
    this.stato = stato;
  }

  public String getLinkCallProposto() {
    return linkCallProposto;
  }

  public void setLinkCallProposto(String linkCallProposto) {
    this.linkCallProposto = linkCallProposto;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RichiestaSupporto that = (RichiestaSupporto) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
