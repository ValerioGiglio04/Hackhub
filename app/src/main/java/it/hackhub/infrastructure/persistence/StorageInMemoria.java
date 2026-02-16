package it.hackhub.infrastructure.persistence;

import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.Sottomissione;
import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Valutazione;
import it.hackhub.core.entities.support.RichiestaSupporto;
import it.hackhub.core.entities.support.SegnalazioneViolazione;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Storage in memoria per le entità.
 */
public class StorageInMemoria {

  private final Map<Long, Hackathon> hackathons = new ConcurrentHashMap<>();
  private final Map<Long, Team> teams = new ConcurrentHashMap<>();
  private final Map<Long, Sottomissione> sottomissioni = new ConcurrentHashMap<>();
  private final Map<Long, Valutazione> valutazioni = new ConcurrentHashMap<>();
  private final Map<Long, RichiestaSupporto> richiesteSupporto = new ConcurrentHashMap<>();
  private final Map<Long, SegnalazioneViolazione> segnalazioniViolazione = new ConcurrentHashMap<>();
  /** hackathonId -> set di teamId iscritti */
  private final Map<Long, Set<Long>> hackathonIscrizioni = new ConcurrentHashMap<>();

  private final AtomicLong nextHackathonId = new AtomicLong(1);
  private final AtomicLong nextTeamId = new AtomicLong(1);
  private final AtomicLong nextSottomissioneId = new AtomicLong(1);
  private final AtomicLong nextValutazioneId = new AtomicLong(1);
  private final AtomicLong nextRichiestaSupportoId = new AtomicLong(1);
  private final AtomicLong nextSegnalazioneViolazioneId = new AtomicLong(1);

  public Map<Long, Hackathon> getHackathons() {
    return hackathons;
  }

  public Map<Long, Team> getTeams() {
    return teams;
  }

  public Map<Long, Sottomissione> getSottomissioni() {
    return sottomissioni;
  }

  public Map<Long, Set<Long>> getHackathonIscrizioni() {
    return hackathonIscrizioni;
  }

  public Map<Long, Valutazione> getValutazioni() {
    return valutazioni;
  }

  public Map<Long, RichiestaSupporto> getRichiesteSupporto() {
    return richiesteSupporto;
  }

  public Map<Long, SegnalazioneViolazione> getSegnalazioniViolazione() {
    return segnalazioniViolazione;
  }

  public long nextHackathonId() {
    return nextHackathonId.getAndIncrement();
  }

  public long nextTeamId() {
    return nextTeamId.getAndIncrement();
  }

  public long nextSottomissioneId() {
    return nextSottomissioneId.getAndIncrement();
  }

  public long nextValutazioneId() {
    return nextValutazioneId.getAndIncrement();
  }

  public long nextRichiestaSupportoId() {
    return nextRichiestaSupportoId.getAndIncrement();
  }

  public long nextSegnalazioneViolazioneId() {
    return nextSegnalazioneViolazioneId.getAndIncrement();
  }
}
