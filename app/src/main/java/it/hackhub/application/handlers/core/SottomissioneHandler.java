package it.hackhub.application.handlers.core;

import it.hackhub.application.dto.SottomissioneCreateDTO;
import it.hackhub.application.dto.SottomissioneUpdateDTO;
import it.hackhub.application.exceptions.EntityNotFoundException;
import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.SottomissioneRepository;
import it.hackhub.application.repositories.TeamRepository;
import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.Sottomissione;
import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Handler per la gestione delle operazioni business logic sulle Sottomissioni.
 */
public class SottomissioneHandler {

    private final SottomissioneRepository sottomissioneRepository;
    private final HackathonRepository hackathonRepository;
    private final TeamRepository teamRepository;

    public SottomissioneHandler(SottomissioneRepository sottomissioneRepository,
                               HackathonRepository hackathonRepository,
                               TeamRepository teamRepository) {
        this.sottomissioneRepository = sottomissioneRepository;
        this.hackathonRepository = hackathonRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Ottiene le sottomissioni per un hackathon.
     * @param hackathonId l'ID dell'hackathon
     * @return lista delle sottomissioni
     * @throws EntityNotFoundException se l'hackathon non esiste
     */
    public List<Sottomissione> ottieniSottomissioniPerHackathon(Long hackathonId) {
        Optional<Hackathon> hackathonOpt = hackathonRepository.findById(hackathonId);
        if (hackathonOpt.isEmpty()) {
            throw new EntityNotFoundException("Hackathon non trovato con ID: " + hackathonId);
        }

        return sottomissioneRepository.findByHackathonId(hackathonId);
    }

    /**
     * Invia una nuova sottomissione.
     * @param dto i dati della sottomissione
     * @param utente l'utente che invia la sottomissione
     * @return la sottomissione creata
     * @throws EntityNotFoundException se l'hackathon o il team non esistono
     * @throws UnauthorizedException se l'utente non è membro del team
     */
    public Sottomissione inviaSottomissione(SottomissioneCreateDTO dto, Utente utente) {
        // Verifica esistenza hackathon
        Optional<Hackathon> hackathonOpt = hackathonRepository.findById(dto.getHackathonId());
        if (hackathonOpt.isEmpty()) {
            throw new EntityNotFoundException("Hackathon non trovato con ID: " + dto.getHackathonId());
        }

        // Verifica esistenza team
        Optional<Team> teamOpt = teamRepository.findById(dto.getTeamId());
        if (teamOpt.isEmpty()) {
            throw new EntityNotFoundException("Team non trovato con ID: " + dto.getTeamId());
        }

        Team team = teamOpt.get();
        
        // Verifica che l'utente sia membro del team
        if (!team.contieneUtente(utente)) {
            throw new UnauthorizedException("L'utente non è membro del team");
        }

        // Crea la sottomissione
        Sottomissione sottomissione = new Sottomissione();
        sottomissione.setTeamId(dto.getTeamId());
        sottomissione.setHackathonId(dto.getHackathonId());
        sottomissione.setLinkProgetto(dto.getLinkProgetto());
        sottomissione.setDataCaricamento(LocalDateTime.now());
        sottomissione.setDataUltimoUpdate(LocalDateTime.now());

        return sottomissioneRepository.save(sottomissione);
    }

    /**
     * Aggiorna una sottomissione esistente.
     * @param id l'ID della sottomissione
     * @param dto i dati aggiornati
     * @param utente l'utente che aggiorna la sottomissione
     * @return la sottomissione aggiornata
     * @throws EntityNotFoundException se la sottomissione non esiste
     * @throws UnauthorizedException se l'utente non è autorizzato ad aggiornare
     */
    public Sottomissione aggiornaSottomissione(Long id, SottomissioneUpdateDTO dto, Utente utente) {
        Optional<Sottomissione> sottomissioneOpt = sottomissioneRepository.findById(id);
        if (sottomissioneOpt.isEmpty()) {
            throw new EntityNotFoundException("Sottomissione non trovata con ID: " + id);
        }

        Sottomissione sottomissione = sottomissioneOpt.get();

        // Verifica che l'utente sia membro del team della sottomissione
        Optional<Team> teamOpt = teamRepository.findById(sottomissione.getTeamId());
        if (teamOpt.isEmpty()) {
            throw new EntityNotFoundException("Team non trovato con ID: " + sottomissione.getTeamId());
        }

        Team team = teamOpt.get();
        if (!team.contieneUtente(utente)) {
            throw new UnauthorizedException("L'utente non è autorizzato ad aggiornare questa sottomissione");
        }

        // Aggiorna i dati
        sottomissione.setLinkProgetto(dto.getLinkProgetto());
        sottomissione.setDataUltimoUpdate(LocalDateTime.now());

        return sottomissioneRepository.save(sottomissione);
    }

    /**
     * Ottiene una sottomissione tramite ID.
     * @param id l'ID della sottomissione
     * @return la sottomissione trovata
     * @throws EntityNotFoundException se la sottomissione non esiste
     */
    public Sottomissione ottieniSottomissione(Long id) {
        Optional<Sottomissione> sottomissioneOpt = sottomissioneRepository.findById(id);
        if (sottomissioneOpt.isEmpty()) {
            throw new EntityNotFoundException("Sottomissione non trovata con ID: " + id);
        }
        return sottomissioneOpt.get();
    }
}
