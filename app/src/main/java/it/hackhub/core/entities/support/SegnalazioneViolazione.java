package it.hackhub.core.entities.support;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entità Segnalazione violazione
 */
@Entity
@Table(name = "Segnalazioni_Violazione")
public class SegnalazioneViolazione {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "id_team_segnalato", nullable = false)
  private Long teamSegnalatoId;
  @Column(name = "id_mentore_segnalante", nullable = false)
  private Long mentoreSegnalanteId;
  @Column(name = "id_hackathon", nullable = false)
  private Long hackathonId;
  private String descrizione;
  @Column(name = "data_segnalazione")
  private LocalDateTime dataSegnalazione;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTeamSegnalatoId() {
    return teamSegnalatoId;
  }

  public void setTeamSegnalatoId(Long teamSegnalatoId) {
    this.teamSegnalatoId = teamSegnalatoId;
  }

  public Long getMentoreSegnalanteId() {
    return mentoreSegnalanteId;
  }

  public void setMentoreSegnalanteId(Long mentoreSegnalanteId) {
    this.mentoreSegnalanteId = mentoreSegnalanteId;
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

  public LocalDateTime getDataSegnalazione() {
    return dataSegnalazione;
  }

  public void setDataSegnalazione(LocalDateTime dataSegnalazione) {
    this.dataSegnalazione = dataSegnalazione;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SegnalazioneViolazione that = (SegnalazioneViolazione) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
