package it.hackhub.presentation.controllers;

import it.hackhub.application.dto.TeamResponseDTO;
import it.hackhub.application.dto.team.GestisciInvitoTeamDTO;
import it.hackhub.application.dto.team.InvitoTeamCreateDTO;
import it.hackhub.application.dto.team.InvitoTeamResponseDTO;
import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.exceptions.core.UnauthorizedException;
import it.hackhub.application.handlers.InvitiHandler;
import it.hackhub.application.handlers.TeamHandler;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.core.entities.associations.InvitoTeam;
import it.hackhub.core.entities.core.Team;
import java.util.List;

/**
 * Controller per inviti team: invita utente, gestisci invito (accetta/rifiuta).
 */
public class InvitiController {

  private final InvitiHandler invitiHandler;
  private final TeamHandler teamHandler;
  private final TeamRepository teamRepository;

  public InvitiController(
    InvitiHandler invitiHandler,
    TeamHandler teamHandler,
    TeamRepository teamRepository
  ) {
    this.invitiHandler = invitiHandler;
    this.teamHandler = teamHandler;
    this.teamRepository = teamRepository;
  }

  /**
   * Invita un utente a unirsi al team (solo il capo del team).
   */
  public InvitoTeamResponseDTO invitaUtente(InvitoTeamCreateDTO dto, Long utenteCorrenteId) {
    if (utenteCorrenteId == null) {
      throw new UnauthorizedException("Utente non autenticato");
    }
    Team team = teamRepository.findById(dto.getTeamId())
        .orElseThrow(() -> new EntityNotFoundException("Team", dto.getTeamId()));
    if (team.getCapo() == null || !team.getCapo().getId().equals(utenteCorrenteId)) {
      throw new UnauthorizedException("Solo il capo del team può invitare");
    }
    InvitoTeam invito = invitiHandler.invitaUtente(
        dto.getTeamId(),
        dto.getUtenteInvitatoId(),
        utenteCorrenteId
    );
    return toInvitoTeamResponseDTO(invito);
  }

  /**
   * Visualizza inviti team ricevuti (PENDING) per l'utente corrente. GET /api/inviti/ricevuti
   */
  public List<InvitoTeamResponseDTO> ottieniInvitiRicevuti(Long utenteCorrenteId) {
    if (utenteCorrenteId == null) {
      throw new UnauthorizedException("Utente non autenticato");
    }
    List<InvitoTeam> inviti = invitiHandler.ottieniInvitiRicevutiPending(utenteCorrenteId);
    return inviti.stream().map(InvitiController::toInvitoTeamResponseDTO).collect(java.util.stream.Collectors.toList());
  }

  /**
   * Gestisce un invito: ACCETTA o RIFIUTA. Ritorna TeamResponseDTO se accetta, InvitoTeamResponseDTO se rifiuta.
   */
  public Object gestisciInvito(Long invitoId, GestisciInvitoTeamDTO dto, Long utenteCorrenteId) {
    if (dto.getAzione() == null || dto.getAzione().isBlank()) {
      throw new BusinessLogicException("Azione non valida");
    }
    String azione = dto.getAzione().toUpperCase();
    switch (azione) {
      case "ACCETTA":
        Team team = invitiHandler.accettaInvito(invitoId, utenteCorrenteId);
        return teamHandler.toResponseDTO(team);
      case "RIFIUTA":
        InvitoTeam invito = invitiHandler.rifiutaInvito(invitoId, utenteCorrenteId);
        return toInvitoTeamResponseDTO(invito);
      default:
        throw new BusinessLogicException("Azione non valida: " + dto.getAzione());
    }
  }

  private static InvitoTeamResponseDTO toInvitoTeamResponseDTO(InvitoTeam invito) {
    InvitoTeamResponseDTO out = new InvitoTeamResponseDTO();
    out.setId(invito.getId());
    out.setStato(invito.getStato());
    out.setDataInvito(invito.getDataInvito());
    if (invito.getTeam() != null) {
      out.setTeamId(invito.getTeam().getId());
      out.setNomeTeam(invito.getTeam().getNome());
      if (invito.getTeam().getCapo() != null) {
        out.setMittenteCapoId(invito.getTeam().getCapo().getId());
      }
    }
    if (invito.getUtenteInvitato() != null) {
      out.setUtenteInvitatoId(invito.getUtenteInvitato().getId());
      String nome = invito.getUtenteInvitato().getNome();
      String cognome = invito.getUtenteInvitato().getCognome();
      out.setUtenteInvitatoNome(nome != null && cognome != null ? nome + " " + cognome : (nome != null ? nome : cognome));
    }
    return out;
  }
}
