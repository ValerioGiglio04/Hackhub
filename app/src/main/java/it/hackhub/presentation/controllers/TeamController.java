package it.hackhub.presentation.controllers;

import it.hackhub.application.dto.TeamCreateDTO;
import it.hackhub.application.dto.TeamResponseDTO;
import it.hackhub.application.dto.UtenteDTO;
import it.hackhub.application.dto.team.TeamCaptainDTO;
import it.hackhub.application.dto.team.TeamHackathonDTO;
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

    /**
     * Iscrive il team a un hackathon (Use case: Iscrive team ad Hackathon). Solo membro o capo del team.
     */
    public void iscriviAdHackathon(TeamHackathonDTO dto, Long utenteCorrenteId) {
        if (utenteCorrenteId == null) {
            throw new UnauthorizedException("Utente non autenticato");
        }
        Team team = teamRepository.findByIdWithCapoAndMembri(dto.getTeamId())
                .orElseThrow(() -> new EntityNotFoundException("Team", dto.getTeamId()));
        boolean isMember = (team.getCapo() != null && team.getCapo().getId().equals(utenteCorrenteId))
                || (team.getMembri() != null && team.getMembri().stream().anyMatch(m -> m.getId().equals(utenteCorrenteId)));
        if (!isMember) {
            throw new UnauthorizedException("Solo un membro o il capo del team può iscrivere il team all'hackathon");
        }
        teamHandler.iscriviAdHackathon(dto.getTeamId(), dto.getHackathonId());
    }

    /**
     * Nomina nuovo capo del team (Use case: Nomina nuovo capo). Solo il capo attuale può farlo.
     */
    public TeamResponseDTO nominaNuovoCapo(TeamCaptainDTO dto, Long utenteCorrenteId) {
        if (utenteCorrenteId == null) {
            throw new UnauthorizedException("Utente non autenticato");
        }
        Team team = teamRepository.findByIdWithCapoAndMembri(dto.getTeamId())
                .orElseThrow(() -> new EntityNotFoundException("Team", dto.getTeamId()));
        if (team.getCapo() == null || !team.getCapo().getId().equals(utenteCorrenteId)) {
            throw new UnauthorizedException("Solo il capo del team può nominare un nuovo capo");
        }
        Team aggiornato = teamHandler.nominaNuovoCapo(dto.getTeamId(), dto.getNuovoCapoId());
        return teamHandler.toResponseDTO(aggiornato);
    }

    /**
     * Rimuove un membro dal team (Use case: Rimuove membro team). Solo il capo può farlo.
     */
    public void rimuoviMembroTeam(Long teamId, Long userId, Long utenteCorrenteId) {
        if (utenteCorrenteId == null) {
            throw new UnauthorizedException("Utente non autenticato");
        }
        Team team = teamRepository.findByIdWithCapoAndMembri(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team", teamId));
        if (team.getCapo() == null || !team.getCapo().getId().equals(utenteCorrenteId)) {
            throw new UnauthorizedException("Solo il capo del team può rimuovere membri");
        }
        teamHandler.rimuoviMembro(teamId, userId);
    }
}
