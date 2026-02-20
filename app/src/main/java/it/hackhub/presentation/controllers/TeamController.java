package it.hackhub.presentation.controllers;

import it.hackhub.application.dto.TeamResponseDTO;
import it.hackhub.application.dto.UtenteDTO;
import it.hackhub.application.exceptions.EntityNotFoundException;
import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.application.handlers.TeamHandler;
import it.hackhub.core.entities.core.Utente;

import java.util.List;

/**
 * Controller per la gestione delle operazioni sui Team.
 */
public class TeamController {

    private final TeamHandler teamHandler;

    public TeamController(TeamHandler teamHandler) {
        this.teamHandler = teamHandler;
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
