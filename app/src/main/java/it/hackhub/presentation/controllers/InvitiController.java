package it.hackhub.presentation.controllers;

import it.hackhub.application.dto.common.StandardResponse;
import it.hackhub.application.dto.team.GestisciInvitoTeamDTO;
import it.hackhub.application.dto.team.InvitoTeamCreateDTO;
import it.hackhub.application.dto.team.InvitoTeamResponseDTO;
import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.handlers.InvitiHandler;
import it.hackhub.application.mappers.TeamDtoMapper;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.associations.InvitoTeam;
import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.infrastructure.security.AuthorizationUtils;
import it.hackhub.infrastructure.security.annotations.RequiresRole;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;

/**
 * Controller Inviti team: logica e check allineati al progetto di riferimento.
 * Invita membro (solo capo), visualizza inviti ricevuti, inviti pendenti per team, gestisci (accetta/rifiuta).
 */
@RestController
@RequestMapping("/api/inviti")
public class InvitiController {

  private final InvitiHandler invitiHandler;
  private final TeamDtoMapper teamDtoMapper;
  private final UtenteRepository utenteRepository;
  private final TeamRepository teamRepository;

  public InvitiController(
    InvitiHandler invitiHandler,
    TeamDtoMapper teamDtoMapper,
    UtenteRepository utenteRepository,
    TeamRepository teamRepository
  ) {
    this.invitiHandler = invitiHandler;
    this.teamDtoMapper = teamDtoMapper;
    this.utenteRepository = utenteRepository;
    this.teamRepository = teamRepository;
  }

  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO, requiresTeamLeader = true)
  @PostMapping("/invia-invito")
  public StandardResponse<InvitoTeamResponseDTO> invitaUtente(
    @Valid @RequestBody InvitoTeamCreateDTO dto
  ) {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    AuthorizationUtils.requireTeamLeader(
      utente,
      dto.getTeamId(),
      teamRepository
    );
    InvitoTeam invito = invitiHandler.invitaUtente(
      dto.getTeamId(),
      dto.getUtenteInvitatoId(),
      utente.getId()
    );
    return StandardResponse.success(teamDtoMapper.toResponseDTO(invito));
  }

  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @GetMapping("/ricevuti")
  public List<InvitoTeamResponseDTO> ottieniInvitiRicevuti() {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    return invitiHandler
      .ottieniInvitiRicevutiPending(utente.getId())
      .stream()
      .map(teamDtoMapper::toResponseDTO)
      .collect(Collectors.toList());
  }

  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO, requiresTeamLeader = true)
  @GetMapping("/team/{teamId}")
  public List<InvitoTeamResponseDTO> ottieniInvitiPendingByTeam(
    @PathVariable Long teamId
  ) {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    AuthorizationUtils.requireTeamLeader(utente, teamId, teamRepository);
    return invitiHandler
      .ottieniInvitiPendingByTeam(teamId)
      .stream()
      .map(teamDtoMapper::toResponseDTO)
      .collect(Collectors.toList());
  }

  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @PostMapping("/{invitoId}/gestisci-invito")
  public StandardResponse<Object> gestisciInvito(
    @PathVariable Long invitoId,
    @Valid @RequestBody GestisciInvitoTeamDTO dto
  ) {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    if (dto.getAzione() == null || dto.getAzione().isBlank()) {
      throw new BusinessLogicException("Azione non valida: " + dto.getAzione());
    }
    //Se l'utente fa gia' parte del team in cui e' stato invitato, non puo' gestire l'invito
    invitiHandler.verificaUtenteNonMembroDelTeamInvito(
      invitoId,
      utente.getId()
    );
    switch (dto.getAzione().toUpperCase()) {
      case "ACCETTA":
        Team team = invitiHandler.accettaInvito(invitoId, utente.getId());
        return StandardResponse.success(teamDtoMapper.toResponseDTO(team));
      case "RIFIUTA":
        InvitoTeam invito = invitiHandler.rifiutaInvito(
          invitoId,
          utente.getId()
        );
        return StandardResponse.success(teamDtoMapper.toResponseDTO(invito));
      default:
        throw new BusinessLogicException(
          "Azione non valida: " + dto.getAzione()
        );
    }
  }
}
