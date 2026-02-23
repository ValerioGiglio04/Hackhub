package it.hackhub.infrastructure.security;

import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.core.entities.associations.StaffHackaton;
import it.hackhub.application.repositories.associations.StaffHackatonRepository;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Classe di utilità per i controlli di autorizzazione basati sui ruoli (RBAC).
 */
public class AuthorizationUtils {

  private AuthorizationUtils() {}

  /**
   * Ottiene l'utente corrente autenticato dal contesto di sicurezza.
   */
  public static Utente getCurrentUser(UtenteRepository utenteRepository) {
    Authentication authentication = SecurityContextHolder
      .getContext()
      .getAuthentication();
    String email = authentication.getName();
    return utenteRepository
      .findByEmail(email)
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Utente con email " + email + " non trovato"
        )
      );
  }

  /**
   * Verifica che l'utente corrente sia un ORGANIZZATORE.
   */
  public static void requireOrganizzatore(Utente utente) {
    if (utente.getRuolo() != Utente.RuoloStaff.ORGANIZZATORE) {
      throw new UnauthorizedException(
        "Solo gli organizzatori possono eseguire questa operazione."
      );
    }
  }

  /**
   * Verifica che l'utente sia un GIUDICE assegnato all'hackathon specificato.
   */
  public static void requireGiudiceOfHackathon(
    Utente utente,
    Long hackathonId,
    HackathonRepository hackathonRepository,
    StaffHackatonRepository staffHackatonRepository
  ) {
    hackathonRepository
      .findById(hackathonId)
      .orElseThrow(() -> new EntityNotFoundException("Hackathon", hackathonId));
    boolean isGiudice = isStaffWithRole(hackathonId, utente.getId(), Utente.RuoloStaff.GIUDICE, staffHackatonRepository);
    if (!isGiudice) {
      throw new UnauthorizedException(
        "Solo i giudici assegnati a questo hackathon possono eseguire questa operazione."
      );
    }
  }

  /**
   * Verifica che l'utente sia un MENTORE assegnato all'hackathon specificato.
   */
  public static void requireMentoreOfHackathon(
    Utente utente,
    Long hackathonId,
    HackathonRepository hackathonRepository,
    StaffHackatonRepository staffHackatonRepository
  ) {
    hackathonRepository
      .findById(hackathonId)
      .orElseThrow(() -> new EntityNotFoundException("Hackathon", hackathonId));
    boolean isMentore = isStaffWithRole(hackathonId, utente.getId(), Utente.RuoloStaff.MENTORE, staffHackatonRepository);
    if (!isMentore) {
      throw new UnauthorizedException(
        "Solo i mentori assegnati a questo hackathon possono eseguire questa operazione."
      );
    }
  }

  /**
   * Verifica che l'utente sia ORGANIZZATORE dell'hackathon specificato.
   */
  public static void requireOrganizzatoreOfHackathon(
    Utente utente,
    Long hackathonId,
    HackathonRepository hackathonRepository,
    StaffHackatonRepository staffHackatonRepository
  ) {
    hackathonRepository
      .findById(hackathonId)
      .orElseThrow(() -> new EntityNotFoundException("Hackathon", hackathonId));
    boolean isOrganizzatore = isStaffWithRole(hackathonId, utente.getId(), Utente.RuoloStaff.ORGANIZZATORE, staffHackatonRepository);
    if (!isOrganizzatore) {
      throw new UnauthorizedException(
        "Solo gli organizzatori di questo hackathon possono eseguire questa operazione."
      );
    }
  }

  private static boolean isStaffWithRole(
    Long hackathonId,
    Long utenteId,
    Utente.RuoloStaff ruolo,
    StaffHackatonRepository staffHackatonRepository
  ) {
    List<StaffHackaton> staff = staffHackatonRepository.findByHackathonId(hackathonId);
    return staff != null && staff.stream()
      .anyMatch(sh -> sh.getUtente() != null
        && utenteId.equals(sh.getUtente().getId())
        && ruolo == sh.getUtente().getRuolo());
  }

  /**
   * Verifica che l'utente sia il capo del team specificato.
   */
  public static void requireTeamLeader(
    Utente utente,
    Long teamId,
    TeamRepository teamRepository
  ) {
    Team team = teamRepository
      .findById(teamId)
      .orElseThrow(() -> new EntityNotFoundException("Team", teamId));
    if (team.getCapo() == null || !team.getCapo().getId().equals(utente.getId())) {
      throw new UnauthorizedException(
        "Solo il capo del team può eseguire questa operazione."
      );
    }
  }

  /**
   * Verifica che l'utente sia membro del team specificato (capo o membro).
   */
  public static void requireTeamMember(
    Utente utente,
    Long teamId,
    TeamRepository teamRepository
  ) {
    Team team = teamRepository
      .findById(teamId)
      .orElseThrow(() -> new EntityNotFoundException("Team", teamId));
    boolean isCapo = team.getCapo() != null && team.getCapo().getId().equals(utente.getId());
    boolean isMembro = team.getMembri() != null
      && team.getMembri().stream().anyMatch(m -> m.getId().equals(utente.getId()));
    if (!isCapo && !isMembro) {
      throw new UnauthorizedException(
        "Solo i membri del team possono eseguire questa operazione."
      );
    }
  }
}
