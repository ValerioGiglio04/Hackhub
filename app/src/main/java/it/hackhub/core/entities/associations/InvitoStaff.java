package it.hackhub.core.entities.associations;

import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.Utente;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Invito a un utente a far parte dello staff di un hackathon.
 */
@Entity
@Table(name = "Inviti_Staff")
public class InvitoStaff {

  public enum StatoInvito {
    PENDING,
    ACCETTATO,
    RIFIUTATO,
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "id_hackathon", nullable = false)
  private Hackathon hackathon;
  @ManyToOne
  @JoinColumn(name = "id_utente_invitato", nullable = false)
  private Utente utenteInvitato;
  @ManyToOne
  @JoinColumn(name = "id_mittente", nullable = false)
  private Utente mittente;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatoInvito stato = StatoInvito.PENDING;
  @Column(name = "data_invito", nullable = false)
  private LocalDateTime dataInvito;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Hackathon getHackathon() { return hackathon; }
  public void setHackathon(Hackathon hackathon) { this.hackathon = hackathon; }
  public Utente getUtenteInvitato() { return utenteInvitato; }
  public void setUtenteInvitato(Utente utenteInvitato) { this.utenteInvitato = utenteInvitato; }
  public Utente getMittente() { return mittente; }
  public void setMittente(Utente mittente) { this.mittente = mittente; }
  public StatoInvito getStato() { return stato; }
  public void setStato(StatoInvito stato) { this.stato = stato; }
  public LocalDateTime getDataInvito() { return dataInvito; }
  public void setDataInvito(LocalDateTime dataInvito) { this.dataInvito = dataInvito; }

  @PrePersist
  protected void onCreate() {
    if (dataInvito == null) dataInvito = LocalDateTime.now();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InvitoStaff that = (InvitoStaff) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
