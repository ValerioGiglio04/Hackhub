package it.hackhub.application.handlers;

import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.exceptions.team.UserAlreadyInTeamException;
import it.hackhub.application.handlers.TeamHandler;
import it.hackhub.application.repositories.associations.InvitoTeamRepository;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.associations.InvitoTeam;
import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * Handler per inviti a unirsi a un team (invita, accetta, rifiuta).
 */
@Service
public class InvitiHandler {

  private static final int MAX_MEMBRI_TEAM = 5;

  private final InvitoTeamRepository invitoTeamRepository;
  private final TeamRepository teamRepository;
  private final UtenteRepository utenteRepository;
  private final TeamHandler teamHandler;

  public InvitiHandler(
    InvitoTeamRepository invitoTeamRepository,
    TeamRepository teamRepository,
    UtenteRepository utenteRepository,
    TeamHandler teamHandler
  ) {
    this.invitoTeamRepository = invitoTeamRepository;
    this.teamRepository = teamRepository;
    this.utenteRepository = utenteRepository;
    this.teamHandler = teamHandler;
  }

  public InvitoTeam invitaUtente(Long teamId, Long utenteInvitatoId, Long capoId) {
    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new EntityNotFoundException("Team", teamId));
    Utente utenteInvitato = utenteRepository.findById(utenteInvitatoId)
        .orElseThrow(() -> new EntityNotFoundException("Utente", utenteInvitatoId));
    if (team.getCapo() == null || !team.getCapo().getId().equals(capoId)) {
      throw new BusinessLogicException("Solo il capo del team può invitare");
    }
    if (utenteInvitatoId.equals(capoId)) {
      throw new BusinessLogicException("Non puoi invitare te stesso");
    }
    if (team.contieneUtente(utenteInvitato)) {
      throw new BusinessLogicException("L'utente è già nel team");
    }
    if (utenteAppartieneAUnTeam(utenteInvitatoId)) {
      throw new UserAlreadyInTeamException(utenteInvitatoId);
    }
    Optional<InvitoTeam> esistente = invitoTeamRepository.findByTeamIdAndUtenteInvitatoIdAndStato(
        teamId, utenteInvitatoId, InvitoTeam.StatoInvito.PENDING);
    if (esistente.isPresent()) {
      throw new BusinessLogicException("Esiste già un invito pendente per questo utente");
    }
    int membri = team.getMembri() != null ? team.getMembri().size() : 0;
    if (membri >= MAX_MEMBRI_TEAM) {
      throw new BusinessLogicException("Il team ha raggiunto il numero massimo di membri");
    }
    InvitoTeam invito = new InvitoTeam();
    invito.setTeam(team);
    invito.setUtenteInvitato(utenteInvitato);
    invito.setStato(InvitoTeam.StatoInvito.PENDING);
    invito.setDataInvito(LocalDateTime.now());
    return invitoTeamRepository.save(invito);
  }

  public Team accettaInvito(Long invitoId, Long utenteCorrenteId) {
    InvitoTeam invito = invitoTeamRepository.findByIdWithDetails(invitoId)
        .orElseThrow(() -> new EntityNotFoundException("Invito", invitoId));
    if (invito.getStato() != InvitoTeam.StatoInvito.PENDING) {
      throw new BusinessLogicException("Questo invito non è più valido");
    }
    if (!invito.getUtenteInvitato().getId().equals(utenteCorrenteId)) {
      throw new BusinessLogicException("Puoi accettare solo gli inviti rivolti a te");
    }
    if (utenteAppartieneAUnTeam(utenteCorrenteId)) {
      throw new UserAlreadyInTeamException(utenteCorrenteId);
    }
    teamHandler.aggiungiMembro(invito.getTeam().getId(), invito.getUtenteInvitato().getId());
    invito.setStato(InvitoTeam.StatoInvito.ACCETTATO);
    invitoTeamRepository.save(invito);
    return teamRepository.findById(invito.getTeam().getId()).orElse(invito.getTeam());
  }

  public InvitoTeam rifiutaInvito(Long invitoId, Long utenteCorrenteId) {
    InvitoTeam invito = invitoTeamRepository.findByIdWithDetails(invitoId)
        .orElseThrow(() -> new EntityNotFoundException("Invito", invitoId));
    if (invito.getStato() != InvitoTeam.StatoInvito.PENDING) {
      throw new BusinessLogicException("Questo invito non è più valido");
    }
    if (!invito.getUtenteInvitato().getId().equals(utenteCorrenteId)) {
      throw new BusinessLogicException("Puoi rifiutare solo gli inviti rivolti a te");
    }
    invito.setStato(InvitoTeam.StatoInvito.RIFIUTATO);
    return invitoTeamRepository.save(invito);
  }

  public List<InvitoTeam> ottieniInvitiRicevutiPending(Long utenteId) {
    return invitoTeamRepository.findByUtenteInvitatoIdAndStato(utenteId, InvitoTeam.StatoInvito.PENDING);
  }

  private boolean utenteAppartieneAUnTeam(Long userId) {
    return teamRepository.findByMembroOrCapoId(userId).isPresent();
  }
}
