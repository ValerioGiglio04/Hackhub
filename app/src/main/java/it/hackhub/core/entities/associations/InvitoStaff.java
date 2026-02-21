package it.hackhub.core.entities.associations;

import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.Utente;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Invito a un utente a far parte dello staff di un hackathon (POJO, no JPA).
 */
public class InvitoStaff {

  public enum StatoInvito {
    PENDING,
    ACCETTATO,
    RIFIUTATO,
  }

  private Long id;
  private Hackathon hackathon;
  private Utente utenteInvitato;
  private Utente mittente;
  private StatoInvito stato = StatoInvito.PENDING;
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
