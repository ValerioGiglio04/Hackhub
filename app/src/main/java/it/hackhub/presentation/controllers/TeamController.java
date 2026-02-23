package it.hackhub.presentation.controllers;

import it.hackhub.application.dto.TeamCreateDTO;
import it.hackhub.application.dto.TeamResponseDTO;
import it.hackhub.application.dto.UtenteDTO;
import it.hackhub.application.dto.team.TeamCaptainDTO;
import it.hackhub.application.dto.team.TeamHackathonDTO;
import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.exceptions.team.UserAlreadyInTeamException;
import it.hackhub.application.handlers.InvitiHandler;
import it.hackhub.application.handlers.TeamHandler;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.infrastructure.security.AuthorizationUtils;
import it.hackhub.infrastructure.security.annotations.RequiresRole;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST per la gestione dei Team.
 */
@RestController
@RequestMapping("/api/team")
public class TeamController {

  private final TeamHandler teamHandler;
  private final TeamRepository teamRepository;
  private final UtenteRepository utenteRepository;
  private final InvitiHandler invitiHandler;

  @Autowired
  public TeamController(
    TeamHandler teamHandler,
    TeamRepository teamRepository,
    UtenteRepository utenteRepository,
    InvitiHandler invitiHandler
  ) {
    this.teamHandler = teamHandler;
    this.teamRepository = teamRepository;
    this.utenteRepository = utenteRepository;
    this.invitiHandler = invitiHandler;
  }

  /** @requiresRole Richiede autenticazione (qualsiasi ruolo) */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @GetMapping
  public List<TeamResponseDTO> ottieniTuttiITeam() {
    return teamHandler
      .ottieniTuttiITeam()
      .stream()
      .map(teamHandler::toResponseDTO)
      .collect(Collectors.toList());
  }

  /** @requiresRole Richiede autenticazione (qualsiasi ruolo) */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @PostMapping("/crea")
  public TeamResponseDTO creaTeam(@RequestBody TeamCreateDTO dto) {
    Utente utenteCorrente = AuthorizationUtils.getCurrentUser(utenteRepository);
    //E' necessario verificare che l'utente non sia già membro di un team
    //perche' altrimenti l'utente potrebbe creare un team di cui e' già membro
    AuthorizationUtils.requireUtenteNonMembroDiUnTeam(
      utenteCorrente,
      teamRepository
    );
    Utente capo = utenteRepository
      .findById(dto.getCapoId())
      .orElseThrow(() -> new EntityNotFoundException("Utente", dto.getCapoId())
      );
    Team team = new Team();
    team.setNome(dto.getNome());
    team.setCapo(capo);
    team.setMembri(new ArrayList<>());
    Team creato = teamHandler.creaTeam(team);
    if (
      invitiHandler != null &&
      creato.getCapo() != null &&
      creato.getCapo().getId().equals(utenteCorrente.getId()) &&
      dto.getUtentiDaInvitareIds() != null &&
      !dto.getUtentiDaInvitareIds().isEmpty()
    ) {
      for (Long userId : dto.getUtentiDaInvitareIds()) {
        if (userId.equals(utenteCorrente.getId())) continue;
        try {
          invitiHandler.invitaUtente(
            creato.getId(),
            userId,
            utenteCorrente.getId()
          );
        } catch (Exception ignored) {
          // salta inviti non validi
        }
      }
    }
    return teamHandler.toResponseDTO(creato);
  }

  /** @requiresRole Richiede che l'utente sia membro del team (capo o membro) */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO, requiresTeamMember = true)
  @GetMapping("/{teamId}/membri")
  public List<UtenteDTO> ottieniMembri(@PathVariable Long teamId) {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    //E' necessario verificare che l'utente sia membro del team
    //perche' altrimenti l'utente potrebbe vedere i membri di altri team
    AuthorizationUtils.requireTeamMember(utente, teamId, teamRepository);
    try {
      return teamHandler.ottieniMembri(teamId, utente);
    } catch (EntityNotFoundException e) {
      throw new RuntimeException("Team non trovato: " + teamId, e);
    } catch (UnauthorizedException e) {
      throw new RuntimeException("Utente non autorizzato", e);
    }
  }

  /** @requiresRole Richiede che l'utente sia membro del team (capo o membro) */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO, requiresTeamMember = true)
  @GetMapping("/{teamId}")
  public TeamResponseDTO ottieniTeam(@PathVariable Long teamId) {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    //E' necessario verificare che l'utente sia membro del team
    //perche' altrimenti l'utente potrebbe vedere i membri di altri team
    AuthorizationUtils.requireTeamMember(utente, teamId, teamRepository);
    try {
      return teamHandler.ottieniTeam(teamId, utente);
    } catch (EntityNotFoundException e) {
      throw new RuntimeException("Team non trovato: " + teamId, e);
    } catch (UnauthorizedException e) {
      throw new RuntimeException("Utente non autorizzato", e);
    }
  }

  /** @requiresRole Richiede che l'utente sia membro del team (capo o membro) */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO, requiresTeamMember = true)
  @PostMapping("/{teamId}/abbandona")
  public void abbandonaTeam(@PathVariable Long teamId) {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    //E' necessario verificare che l'utente sia membro del team
    //perche' altrimenti l'utente potrebbe abbandonare il team di altri team
    AuthorizationUtils.requireTeamMember(utente, teamId, teamRepository);
    try {
      teamHandler.abbandonaTeam(teamId, utente);
    } catch (EntityNotFoundException e) {
      throw new RuntimeException("Team non trovato: " + teamId, e);
    } catch (UnauthorizedException e) {
      throw new RuntimeException("Utente non autorizzato", e);
    }
  }

  /** @requiresRole Richiede che l'utente sia membro del team (capo o membro) */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO, requiresTeamMember = true)
  @PostMapping("/iscrivi-hackathon")
  public void iscriviAdHackathon(@RequestBody TeamHackathonDTO dto) {
    Utente utenteCorrente = AuthorizationUtils.getCurrentUser(utenteRepository);
    //E' necessario verificare che l'utente sia capo del team per iscrivere il team ad un hackathon
    AuthorizationUtils.requireTeamLeader(
      utenteCorrente,
      dto.getTeamId(),
      teamRepository
    );

    teamHandler.iscriviAdHackathon(dto.getTeamId(), dto.getHackathonId());
  }

  /** @requiresRole Richiede che l'utente sia capo del team */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO, requiresTeamLeader = true)
  @PostMapping("/nomina-capo")
  public TeamResponseDTO nominaNuovoCapo(@RequestBody TeamCaptainDTO dto) {
    Utente utenteCorrente = AuthorizationUtils.getCurrentUser(utenteRepository);
    //E' necessario verificare che l'utente sia capo del team per nominare un nuovo capo
    AuthorizationUtils.requireTeamLeader(
      utenteCorrente,
      dto.getTeamId(),
      teamRepository
    );
    Team aggiornato = teamHandler.nominaNuovoCapo(
      dto.getTeamId(),
      dto.getNuovoCapoId()
    );
    return teamHandler.toResponseDTO(aggiornato);
  }

  /** @requiresRole Richiede che l'utente sia capo del team per rimuovere un membro */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO, requiresTeamLeader = true)
  @DeleteMapping("/{teamId}/membri/{userId}")
  public void rimuoviMembroTeam(
    @PathVariable Long teamId,
    @PathVariable Long userId
  ) {
    Utente utenteCorrente = AuthorizationUtils.getCurrentUser(utenteRepository);
    //E' necessario verificare che l'utente sia capo del team per rimuovere un membro
    AuthorizationUtils.requireTeamLeader(
      utenteCorrente,
      teamId,
      teamRepository
    );
    teamHandler.rimuoviMembro(teamId, userId);
  }
}
