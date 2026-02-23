package it.hackhub.core.builders.support;

import it.hackhub.core.entities.support.SegnalazioneViolazione;
import java.time.LocalDateTime;

/**
 * Builder per SegnalazioneViolazione (usa id).
 */
public class SegnalazioneViolazioneBuilder {

  private Long teamSegnalatoId;
  private Long mentoreSegnalanteId;
  private Long hackathonId;
  private String descrizione;
  private LocalDateTime dataSegnalazione;

  public SegnalazioneViolazioneBuilder() {
    this.dataSegnalazione = LocalDateTime.now();
  }

  public SegnalazioneViolazioneBuilder teamSegnalatoId(Long teamSegnalatoId) {
    this.teamSegnalatoId = teamSegnalatoId;
    return this;
  }

  public SegnalazioneViolazioneBuilder mentoreSegnalanteId(Long mentoreSegnalanteId) {
    this.mentoreSegnalanteId = mentoreSegnalanteId;
    return this;
  }

  public SegnalazioneViolazioneBuilder hackathonId(Long hackathonId) {
    this.hackathonId = hackathonId;
    return this;
  }

  public SegnalazioneViolazioneBuilder descrizione(String descrizione) {
    this.descrizione = descrizione;
    return this;
  }

  public SegnalazioneViolazioneBuilder dataSegnalazione(LocalDateTime dataSegnalazione) {
    this.dataSegnalazione = dataSegnalazione;
    return this;
  }

  public SegnalazioneViolazione build() {
    if (teamSegnalatoId == null) {
      throw new IllegalArgumentException("Il team segnalato è obbligatorio");
    }
    if (mentoreSegnalanteId == null) {
      throw new IllegalArgumentException("Il mentore segnalante è obbligatorio");
    }
    if (hackathonId == null) {
      throw new IllegalArgumentException("L'hackathon è obbligatorio");
    }
    if (descrizione == null || descrizione.trim().isEmpty()) {
      throw new IllegalArgumentException("La descrizione è obbligatoria");
    }
    SegnalazioneViolazione s = new SegnalazioneViolazione();
    s.setTeamSegnalatoId(teamSegnalatoId);
    s.setMentoreSegnalanteId(mentoreSegnalanteId);
    s.setHackathonId(hackathonId);
    s.setDescrizione(descrizione);
    s.setDataSegnalazione(dataSegnalazione);
    return s;
  }
}
