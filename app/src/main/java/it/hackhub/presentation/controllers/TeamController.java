package it.hackhub.presentation.controllers;

import it.hackhub.application.dto.TeamCreateDTO;
import it.hackhub.application.dto.TeamResponseDTO;
import it.hackhub.application.dto.UtenteDTO;
import it.hackhub.application.exceptions.EntityNotFoundException;
import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.exceptions.team.UserAlreadyInTeamException;
import it.hackhub.application.handlers.InvitiHandler;
import it.hackhub.application.handlers.TeamHandler;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller per la gestione delle operazioni sui Team.
 */
public class TeamController {

    private final TeamHandler teamHandler;
    private final TeamRepository teamRepository;
    private final UtenteRepository utenteRepository;
    private final InvitiHandler invitiHandler;

    public TeamController(
        TeamHandler teamHandler,
        TeamRepository teamRepository,
        UtenteRepository utenteRepository
    ) {
        this(teamHandler, teamRepository, utenteRepository, null);
    }

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

    /**
     * Crea un team (use case Crea Team). L'utente corrente diventa il capo.
     * Non consentito se l'utente è già in un team.
     * Opzionale: invii agli utenti in utentiDaInvitareIds (solo se capoId = utenteCorrenteId).
     */
    public TeamResponseDTO creaTeam(TeamCreateDTO dto, Long utenteCorrenteId) {
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

    /**
     * Ottiene la lista dei membri di un team.
     */
    public List<UtenteDTO> ottieniMembri(Long teamId, Long utenteId) {
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

    /**
     * Ottiene le informazioni di un team.
     */
    public TeamResponseDTO ottieniTeam(Long teamId, Long utenteId) {
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

    /**
     * Permette a un utente di abbandonare un team.
     */
    public void abbandonaTeam(Long teamId, Long utenteId) {
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
}
