package it.hackhub.core.builders.support;

import it.hackhub.core.entities.support.RichiestaSupporto;
import java.time.LocalDateTime;

/**
 * Builder per RichiestaSupporto (usa teamId e hackathonId).
 */
public class RichiestaSupportoBuilder {

  private Long teamId;
  private Long hackathonId;
  private String descrizione;
  private LocalDateTime dataRichiesta;
  private String stato = "APERTA";

  public RichiestaSupportoBuilder() {
    this.dataRichiesta = LocalDateTime.now();
  }

  public RichiestaSupportoBuilder teamId(Long teamId) {
    this.teamId = teamId;
    return this;
  }

  public RichiestaSupportoBuilder hackathonId(Long hackathonId) {
    this.hackathonId = hackathonId;
    return this;
  }

  public RichiestaSupportoBuilder descrizione(String descrizione) {
    this.descrizione = descrizione;
    return this;
  }

  public RichiestaSupportoBuilder dataRichiesta(LocalDateTime dataRichiesta) {
    this.dataRichiesta = dataRichiesta;
    return this;
  }

  public RichiestaSupportoBuilder stato(String stato) {
    this.stato = stato;
    return this;
  }

  public RichiestaSupporto build() {
    if (teamId == null) {
      throw new IllegalArgumentException("Il team è obbligatorio");
    }
    if (hackathonId == null) {
      throw new IllegalArgumentException("L'hackathon è obbligatorio");
    }
    if (descrizione == null || descrizione.trim().isEmpty()) {
      throw new IllegalArgumentException("La descrizione è obbligatoria");
    }
    RichiestaSupporto r = new RichiestaSupporto();
    r.setTeamId(teamId);
    r.setHackathonId(hackathonId);
    r.setDescrizione(descrizione);
    r.setDataRichiesta(dataRichiesta);
    r.setStato(stato);
    return r;
  }
}
