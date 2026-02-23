package it.hackhub.core.entities.associations;

import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Invito a un utente a unirsi a un team.
 */
@Entity
@Table(name = "Inviti_Team")
public class InvitoTeam {

  public enum StatoInvito {
    PENDING,
    ACCETTATO,
    RIFIUTATO,
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "id_team", nullable = false)
  private Team team;
  @ManyToOne
  @JoinColumn(name = "id_utente_invitato", nullable = false)
  private Utente utenteInvitato;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatoInvito stato = StatoInvito.PENDING;
  @Column(name = "data_invito", nullable = false)
  private LocalDateTime dataInvito;

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

  public Utente getUtenteInvitato() {
    return utenteInvitato;
  }

  public void setUtenteInvitato(Utente utenteInvitato) {
    this.utenteInvitato = utenteInvitato;
  }

  public StatoInvito getStato() {
    return stato;
  }

  public void setStato(StatoInvito stato) {
    this.stato = stato;
  }

  public LocalDateTime getDataInvito() {
    return dataInvito;
  }

  public void setDataInvito(LocalDateTime dataInvito) {
    this.dataInvito = dataInvito;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InvitoTeam that = (InvitoTeam) o;
    return Objects.equals(id, that.id);
  }

  @PrePersist
  protected void onCreate() {
    if (dataInvito == null) dataInvito = LocalDateTime.now();
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
