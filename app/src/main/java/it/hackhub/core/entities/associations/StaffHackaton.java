package it.hackhub.core.entities.associations;

import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.Utente;
import java.util.Objects;

/**
 * Associazione utente–hackathon (utente nello staff dell'hackathon). POJO, no JPA.
 */
public class StaffHackaton {

  private Long id;
  private Hackathon hackathon;
  private Utente utente;

  public StaffHackaton() {}

  public StaffHackaton(Hackathon hackathon, Utente utente) {
    this.hackathon = hackathon;
    this.utente = utente;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Hackathon getHackathon() { return hackathon; }
  public void setHackathon(Hackathon hackathon) { this.hackathon = hackathon; }
  public Utente getUtente() { return utente; }
  public void setUtente(Utente utente) { this.utente = utente; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StaffHackaton that = (StaffHackaton) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
