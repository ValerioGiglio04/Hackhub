package it.hackhub.presentation.controllers;

import it.hackhub.application.dto.team.GestisciInvitoTeamDTO;
import it.hackhub.application.dto.team.InvitoTeamCreateDTO;
import it.hackhub.application.dto.team.InvitoTeamResponseDTO;
import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.handlers.InvitiHandler;
import it.hackhub.application.handlers.TeamHandler;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.associations.InvitoTeam;
import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.infrastructure.security.SecurityUtils;
import it.hackhub.infrastructure.security.annotations.RequiresRole;
import java.util.List;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST per inviti team (invita, gestisci invito).
 */
@RestController
@RequestMapping("/api/inviti")
public class InvitiController {

  private final InvitiHandler invitiHandler;
  private final TeamHandler teamHandler;
  private final TeamRepository teamRepository;
  private final UtenteRepository utenteRepository;

  public InvitiController(
    InvitiHandler invitiHandler,
    TeamHandler teamHandler,
    TeamRepository teamRepository,
    UtenteRepository utenteRepository
  ) {
    this.invitiHandler = invitiHandler;
    this.teamHandler = teamHandler;
    this.teamRepository = teamRepository;
    this.utenteRepository = utenteRepository;
  }

  /** @requiresRole Richiede che l'utente sia capo del team */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO, requiresTeamLeader = true)
  @PostMapping("/invita")
  public InvitoTeamResponseDTO invitaUtente(
    @RequestBody InvitoTeamCreateDTO dto
  ) {
    Long utenteCorrenteId = SecurityUtils.getCurrentUserId(utenteRepository);
    Team team = teamRepository
      .findById(dto.getTeamId())
      .orElseThrow(() -> new EntityNotFoundException("Team", dto.getTeamId()));
    if (
      team.getCapo() == null || !team.getCapo().getId().equals(utenteCorrenteId)
    ) {
      throw new UnauthorizedException("Solo il capo del team può invitare");
    }
    InvitoTeam invito = invitiHandler.invitaUtente(
      dto.getTeamId(),
      dto.getUtenteInvitatoId(),
      utenteCorrenteId
    );
    return toInvitoTeamResponseDTO(invito);
  }

  /** @requiresRole Richiede autenticazione (qualsiasi ruolo) */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @GetMapping("/ricevuti")
  public List<InvitoTeamResponseDTO> ottieniInvitiRicevuti() {
    Long utenteCorrenteId = SecurityUtils.getCurrentUserId(utenteRepository);
    List<InvitoTeam> inviti = invitiHandler.ottieniInvitiRicevutiPending(
      utenteCorrenteId
    );
    return inviti
      .stream()
      .map(InvitiController::toInvitoTeamResponseDTO)
      .collect(java.util.stream.Collectors.toList());
  }

  /** @requiresRole Richiede autenticazione (qualsiasi ruolo) */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @PostMapping("/{invitoId}/gestisci")
  public Object gestisciInvito(
    @PathVariable Long invitoId,
    @RequestBody GestisciInvitoTeamDTO dto
  ) {
    Long utenteCorrenteId = SecurityUtils.getCurrentUserId(utenteRepository);
    if (dto.getAzione() == null || dto.getAzione().isBlank()) {
      throw new BusinessLogicException("Azione non valida");
    }
    String azione = dto.getAzione().toUpperCase();
    switch (azione) {
      case "ACCETTA":
        Team team = invitiHandler.accettaInvito(invitoId, utenteCorrenteId);
        return teamHandler.toResponseDTO(team);
      case "RIFIUTA":
        InvitoTeam invito = invitiHandler.rifiutaInvito(
          invitoId,
          utenteCorrenteId
        );
        return toInvitoTeamResponseDTO(invito);
      default:
        throw new BusinessLogicException(
          "Azione non valida: " + dto.getAzione()
        );
    }
  }

  private static InvitoTeamResponseDTO toInvitoTeamResponseDTO(
    InvitoTeam invito
  ) {
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
      out.setUtenteInvitatoNome(
        nome != null && cognome != null
          ? nome + " " + cognome
          : (nome != null ? nome : cognome)
      );
    }
    return out;
  }
}
