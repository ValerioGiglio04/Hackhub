package it.hackhub.presentation.controllers.core;

import it.hackhub.application.dto.SottomissioneCreateDTO;
import it.hackhub.application.dto.SottomissioneUpdateDTO;
import it.hackhub.application.exceptions.EntityNotFoundException;
import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.application.handlers.core.SottomissioneHandler;
import it.hackhub.core.entities.core.Sottomissione;
import it.hackhub.core.entities.core.Utente;

import java.util.List;

/**
 * Controller per la gestione delle operazioni sulle Sottomissioni.
 */
public class SottomissioniController {

    private final SottomissioneHandler sottomissioneHandler;

    public SottomissioniController(SottomissioneHandler sottomissioneHandler) {
        this.sottomissioneHandler = sottomissioneHandler;
    }

    /**
     * Ottiene le sottomissioni per un hackathon.
     */
    public List<Sottomissione> ottieniSottomissioniPerHackathon(Long hackathonId) {
        try {
            return sottomissioneHandler.ottieniSottomissioniPerHackathon(hackathonId);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Hackathon non trovato: " + hackathonId, e);
        }
    }

    /**
     * Invia una nuova sottomissione.
     */
    public Sottomissione inviaSottomissione(SottomissioneCreateDTO dto, Long utenteId) {
        Utente utente = new Utente();
        utente.setId(utenteId);
        
        try {
            return sottomissioneHandler.inviaSottomissione(dto, utente);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Entità non trovata", e);
        } catch (UnauthorizedException e) {
            throw new RuntimeException("Utente non autorizzato", e);
        }
    }

    /**
     * Aggiorna una sottomissione esistente.
     */
    public Sottomissione aggiornaSottomissione(Long id, SottomissioneUpdateDTO dto, Long utenteId) {
        Utente utente = new Utente();
        utente.setId(utenteId);
        
        try {
            return sottomissioneHandler.aggiornaSottomissione(id, dto, utente);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Sottomissione non trovata: " + id, e);
        } catch (UnauthorizedException e) {
            throw new RuntimeException("Utente non autorizzato", e);
        }
    }

    /**
     * Ottiene una sottomissione tramite ID.
     */
    public Sottomissione ottieniSottomissione(Long id) {
        try {
            return sottomissioneHandler.ottieniSottomissione(id);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Sottomissione non trovata: " + id, e);
        }
    }
}
