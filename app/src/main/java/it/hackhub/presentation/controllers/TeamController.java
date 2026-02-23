package it.hackhub.presentation.controllers;

import it.hackhub.application.dto.TeamCreateDTO;
import it.hackhub.application.dto.TeamResponseDTO;
import it.hackhub.application.dto.UtenteDTO;
import it.hackhub.application.dto.team.TeamCaptainDTO;
import it.hackhub.application.dto.team.TeamHackathonDTO;
import it.hackhub.application.exceptions.EntityNotFoundException;
import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.application.exceptions.team.UserAlreadyInTeamException;
import it.hackhub.application.handlers.InvitiHandler;
import it.hackhub.application.handlers.TeamHandler;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.infrastructure.security.SecurityUtils;
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

    @GetMapping
    public List<TeamResponseDTO> ottieniTuttiITeam() {
        return teamHandler.ottieniTuttiITeam().stream()
                .map(teamHandler::toResponseDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public TeamResponseDTO creaTeam(@RequestBody TeamCreateDTO dto) {
        Long utenteCorrenteId = SecurityUtils.getCurrentUserId(utenteRepository);
        if (teamRepository.findByMembroOrCapoId(utenteCorrenteId).isPresent()) {
            throw new UserAlreadyInTeamException(utenteCorrenteId);
        }
        Utente capo = utenteRepository.findById(dto.getCapoId())
            .orElseThrow(() -> new EntityNotFoundException("Utente", dto.getCapoId()));
        Team team = new Team();
        team.setNome(dto.getNome());
        team.setCapo(capo);
        team.setMembri(new ArrayList<>());
        Team creato = teamHandler.creaTeam(team);
        if (invitiHandler != null && creato.getCapo() != null && creato.getCapo().getId().equals(utenteCorrenteId)
            && dto.getUtentiDaInvitareIds() != null && !dto.getUtentiDaInvitareIds().isEmpty()) {
            for (Long userId : dto.getUtentiDaInvitareIds()) {
                if (userId.equals(utenteCorrenteId)) continue;
                try {
                    invitiHandler.invitaUtente(creato.getId(), userId, utenteCorrenteId);
                } catch (Exception ignored) {
                    // salta inviti non validi
                }
            }
        }
        return teamHandler.toResponseDTO(creato);
    }

    @GetMapping("/{teamId}/membri")
    public List<UtenteDTO> ottieniMembri(@PathVariable Long teamId) {
        Long utenteId = SecurityUtils.getCurrentUserId(utenteRepository);
        Utente utente = new Utente();
        utente.setId(utenteId);
        
        try {
            return teamHandler.ottieniMembri(teamId, utente);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Team non trovato: " + teamId, e);
        } catch (UnauthorizedException e) {
            throw new RuntimeException("Utente non autorizzato", e);
        }
    }

    @GetMapping("/{teamId}")
    public TeamResponseDTO ottieniTeam(@PathVariable Long teamId) {
        Long utenteId = SecurityUtils.getCurrentUserId(utenteRepository);
        Utente utente = new Utente();
        utente.setId(utenteId);
        
        try {
            return teamHandler.ottieniTeam(teamId, utente);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Team non trovato: " + teamId, e);
        } catch (UnauthorizedException e) {
            throw new RuntimeException("Utente non autorizzato", e);
        }
    }

    @PostMapping("/{teamId}/abbandona")
    public void abbandonaTeam(@PathVariable Long teamId) {
        Long utenteId = SecurityUtils.getCurrentUserId(utenteRepository);
        Utente utente = new Utente();
        utente.setId(utenteId);
        
        try {
            teamHandler.abbandonaTeam(teamId, utente);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Team non trovato: " + teamId, e);
        } catch (UnauthorizedException e) {
            throw new RuntimeException("Utente non autorizzato", e);
        }
    }

    @PostMapping("/iscrivi-hackathon")
    public void iscriviAdHackathon(@RequestBody TeamHackathonDTO dto) {
        Long utenteCorrenteId = SecurityUtils.getCurrentUserId(utenteRepository);
        Team team = teamRepository.findByIdWithCapoAndMembri(dto.getTeamId())
                .orElseThrow(() -> new EntityNotFoundException("Team", dto.getTeamId()));
        boolean isMember = (team.getCapo() != null && team.getCapo().getId().equals(utenteCorrenteId))
                || (team.getMembri() != null && team.getMembri().stream().anyMatch(m -> m.getId().equals(utenteCorrenteId)));
        if (!isMember) {
            throw new UnauthorizedException("Solo un membro o il capo del team può iscrivere il team all'hackathon");
        }
        teamHandler.iscriviAdHackathon(dto.getTeamId(), dto.getHackathonId());
    }

    @PostMapping("/nomina-capo")
    public TeamResponseDTO nominaNuovoCapo(@RequestBody TeamCaptainDTO dto) {
        Long utenteCorrenteId = SecurityUtils.getCurrentUserId(utenteRepository);
        Team team = teamRepository.findByIdWithCapoAndMembri(dto.getTeamId())
                .orElseThrow(() -> new EntityNotFoundException("Team", dto.getTeamId()));
        if (team.getCapo() == null || !team.getCapo().getId().equals(utenteCorrenteId)) {
            throw new UnauthorizedException("Solo il capo del team può nominare un nuovo capo");
        }
        Team aggiornato = teamHandler.nominaNuovoCapo(dto.getTeamId(), dto.getNuovoCapoId());
        return teamHandler.toResponseDTO(aggiornato);
    }

    @DeleteMapping("/{teamId}/membri/{userId}")
    public void rimuoviMembroTeam(@PathVariable Long teamId, @PathVariable Long userId) {
        Long utenteCorrenteId = SecurityUtils.getCurrentUserId(utenteRepository);
        Team team = teamRepository.findByIdWithCapoAndMembri(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team", teamId));
        if (team.getCapo() == null || !team.getCapo().getId().equals(utenteCorrenteId)) {
            throw new UnauthorizedException("Solo il capo del team può rimuovere membri");
        }
        teamHandler.rimuoviMembro(teamId, userId);
    }
}
