package it.hackhub.application.handlers.core;

import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.application.exceptions.submission.SottomissioneGiaPresenteException;
import it.hackhub.application.exceptions.submission.SubmissionDeadlinePassedException;
import it.hackhub.application.exceptions.validation.InvalidGitHubLinkException;
import it.hackhub.application.repositories.associations.StaffHackatonRepository;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.SottomissioneRepository;
import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.Sottomissione;
import it.hackhub.core.entities.core.Utente;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Handler per la gestione delle sottomissioni.
 * Logica allineata al progetto di riferimento: scadenza sottomissioni, validazione link GitHub,
 * ottieni per hackathon/team/giudice.
 */
@Service
public class SottomissioneHandler {

  private final SottomissioneRepository sottomissioneRepository;
  private final HackathonRepository hackathonRepository;
  private final StaffHackatonRepository staffHackatonRepository;

  public SottomissioneHandler(
    SottomissioneRepository sottomissioneRepository,
    HackathonRepository hackathonRepository,
    StaffHackatonRepository staffHackatonRepository
  ) {
    this.sottomissioneRepository = sottomissioneRepository;
    this.hackathonRepository = hackathonRepository;
    this.staffHackatonRepository = staffHackatonRepository;
  }

  public Sottomissione inviaSottomissione(Sottomissione sottomissione)
    throws SubmissionDeadlinePassedException, InvalidGitHubLinkException, SottomissioneGiaPresenteException {
    Hackathon hackathon = hackathonRepository
      .findById(sottomissione.getHackathonId())
      .orElseThrow(() -> new EntityNotFoundException("Hackathon", sottomissione.getHackathonId()));
    validaScadenzaSottomissioni(hackathon);
    validaLinkGitHub(sottomissione.getLinkProgetto());
    // Una sola sottomissione per team per hackathon
    if (sottomissioneRepository.findByTeamIdAndHackathonId(sottomissione.getTeamId(), sottomissione.getHackathonId()).isPresent()) {
      throw new SottomissioneGiaPresenteException(sottomissione.getTeamId(), sottomissione.getHackathonId());
    }
    sottomissione.setDataCaricamento(LocalDateTime.now());
    sottomissione.setDataUltimoUpdate(LocalDateTime.now());
    return sottomissioneRepository.save(sottomissione);
  }

  public Sottomissione aggiornaSottomissione(
    Long sottomissioneId,
    Sottomissione sottomissioneAggiornata
  ) throws EntityNotFoundException, SubmissionDeadlinePassedException, InvalidGitHubLinkException {
    Sottomissione esistente = sottomissioneRepository
      .findById(sottomissioneId)
      .orElseThrow(() -> new EntityNotFoundException("Sottomissione", sottomissioneId));
    Hackathon hackathon = hackathonRepository
      .findById(esistente.getHackathonId())
      .orElseThrow(() -> new EntityNotFoundException("Hackathon", esistente.getHackathonId()));
    validaScadenzaSottomissioni(hackathon);
    validaLinkGitHub(sottomissioneAggiornata.getLinkProgetto());
    esistente.setLinkProgetto(sottomissioneAggiornata.getLinkProgetto());
    esistente.setDataUltimoUpdate(LocalDateTime.now());
    return sottomissioneRepository.save(esistente);
  }

  private void validaScadenzaSottomissioni(Hackathon hackathon) {
    if (hackathon.getScadenzaSottomissioni() != null
      && LocalDateTime.now().isAfter(hackathon.getScadenzaSottomissioni())) {
      throw new SubmissionDeadlinePassedException(hackathon.getScadenzaSottomissioni());
    }
  }

  private void validaLinkGitHub(String linkProgetto) {
    if (linkProgetto == null || linkProgetto.trim().isEmpty()) {
      throw new InvalidGitHubLinkException("Il link del progetto GitHub è obbligatorio");
    }
    if (!linkProgetto.matches("^https://github\\.com/.+/.+$")) {
      throw new InvalidGitHubLinkException(
        "Il link deve essere un URL GitHub valido (es: https://github.com/username/repository)");
    }
  }

  public List<Sottomissione> ottieniTutteLeSottomissioni() {
    return sottomissioneRepository.findAll();
  }

  public List<Sottomissione> ottieniSottomissioniPerHackathon(Long hackathonId) {
    hackathonRepository
      .findById(hackathonId)
      .orElseThrow(() -> new EntityNotFoundException("Hackathon", hackathonId));
    return sottomissioneRepository.findByHackathonId(hackathonId);
  }

  public Sottomissione ottieniSottomissionePerId(Long sottomissioneId) {
    return sottomissioneRepository
      .findById(sottomissioneId)
      .orElseThrow(() -> new EntityNotFoundException("Sottomissione", sottomissioneId));
  }

  public List<Sottomissione> ottieniSottomissioniPerTeam(Long teamId) {
    return sottomissioneRepository.findByTeamId(teamId);
  }

  public List<Sottomissione> ottieniSottomissioniPerHackathonEGiudice(
    Long hackathonId,
    Long giudiceUtenteId
  ) throws EntityNotFoundException, UnauthorizedException {
    hackathonRepository
      .findById(hackathonId)
      .orElseThrow(() -> new EntityNotFoundException("Hackathon", hackathonId));
    List<it.hackhub.core.entities.associations.StaffHackaton> staff =
      staffHackatonRepository.findByHackathonId(hackathonId);
    boolean giudiceAssegnato = staff != null && staff.stream()
      .anyMatch(sh -> sh.getUtente() != null
        && sh.getUtente().getId().equals(giudiceUtenteId)
        && sh.getUtente().getRuolo() == Utente.RuoloStaff.GIUDICE);
    if (!giudiceAssegnato) {
      throw new UnauthorizedException("Il giudice non è assegnato a questo hackathon.");
    }
    return sottomissioneRepository.findByHackathonId(hackathonId);
  }
}
