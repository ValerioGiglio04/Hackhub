package it.hackhub.core.entities.associations;

import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.Team;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Iscrizione di un team a un hackathon.
 */
@Entity
@Table(
  name = "Iscrizioni_Team_Hackathon",
  uniqueConstraints = @UniqueConstraint(columnNames = { "id_team", "id_hackathon" })
)
public class IscrizioneTeamHackathon {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "id_team", nullable = false)
  private Team team;

  @ManyToOne
  @JoinColumn(name = "id_hackathon", nullable = false)
  private Hackathon hackathon;

  @Column(name = "data_iscrizione", nullable = false)
  private LocalDateTime dataIscrizione;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public Hackathon getHackathon() {
    return hackathon;
  }

  public void setHackathon(Hackathon hackathon) {
    this.hackathon = hackathon;
  }

  /** Convenience: id del team (per compatibilità con codice che usa getTeamId). */
  public Long getTeamId() {
    return team != null ? team.getId() : null;
  }

  /** Convenience: id dell'hackathon (per compatibilità con codice che usa getHackathonId). */
  public Long getHackathonId() {
    return hackathon != null ? hackathon.getId() : null;
  }

  public LocalDateTime getDataIscrizione() {
    return dataIscrizione;
  }

  public void setDataIscrizione(LocalDateTime dataIscrizione) {
    this.dataIscrizione = dataIscrizione;
  }

  @PrePersist
  protected void onCreate() {
    if (dataIscrizione == null) dataIscrizione = LocalDateTime.now();
  }
}
