package it.hackhub.application.handlers;

import it.hackhub.application.dto.TeamResponseDTO;
import it.hackhub.application.dto.UtenteDTO;
import it.hackhub.application.exceptions.EntityNotFoundException;
import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.application.repositories.associations.IscrizioneTeamHackathonRepository;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.associations.IscrizioneTeamHackathon;
import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.StatoHackathon;
import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handler per la gestione delle operazioni business logic sui Team.
 */
public class TeamHandler {

    private final TeamRepository teamRepository;
    private final UtenteRepository utenteRepository;
    private final HackathonRepository hackathonRepository;
    private final IscrizioneTeamHackathonRepository iscrizioneTeamHackathonRepository;

    public TeamHandler(TeamRepository teamRepository) {
        this(teamRepository, null, null, null);
    }

    public TeamHandler(TeamRepository teamRepository, UtenteRepository utenteRepository) {
        this(teamRepository, utenteRepository, null, null);
    }

    public TeamHandler(
            TeamRepository teamRepository,
            UtenteRepository utenteRepository,
            HackathonRepository hackathonRepository,
            IscrizioneTeamHackathonRepository iscrizioneTeamHackathonRepository) {
        this.teamRepository = teamRepository;
        this.utenteRepository = utenteRepository;
        this.hackathonRepository = hackathonRepository;
        this.iscrizioneTeamHackathonRepository = iscrizioneTeamHackathonRepository;
    }

    /**
     * Crea un team (capo e membri già impostati sull'entità; membri tipicamente vuoti).
     */
    public Team creaTeam(Team team) {
        if (team.getMembri() == null) {
            team.setMembri(new java.util.ArrayList<>());
        }
        return teamRepository.save(team);
    }

    /**
     * Converte un Team in TeamResponseDTO (esposto per uso dal controller).
     */
    public TeamResponseDTO toResponseDTO(Team team) {
        return convertToTeamResponseDTO(team);
    }

    /**
     * Aggiunge un membro al team (usato quando un invito viene accettato).
     */
    public Team aggiungiMembro(Long teamId, Long userId) {
        if (utenteRepository == null) {
            throw new IllegalStateException("UtenteRepository non configurato");
        }
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new EntityNotFoundException("Team", teamId));
        Utente utente = utenteRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Utente", userId));
        if (team.contieneUtente(utente)) {
            throw new it.hackhub.application.exceptions.core.BusinessLogicException("L'utente è già nel team");
        }
        if (team.getMembri() == null) {
            team.setMembri(new java.util.ArrayList<>());
        }
        team.getMembri().add(utente);
        return teamRepository.save(team);
    }

    /**
     * Ottiene i membri di un team.
     * @param teamId l'ID del team
     * @param utente l'utente che effettua la richiesta
     * @return lista di membri del team
     * @throws EntityNotFoundException se il team non esiste
     * @throws UnauthorizedException se l'utente non è membro del team
     */
    public List<UtenteDTO> ottieniMembri(Long teamId, Utente utente) {
        Optional<Team> teamOpt = teamRepository.findByIdWithCapoAndMembri(teamId);
        if (teamOpt.isEmpty()) {
            throw new EntityNotFoundException("Team non trovato con ID: " + teamId);
        }

        Team team = teamOpt.get();
        if (!team.contieneUtente(utente)) {
            throw new UnauthorizedException("L'utente non è membro del team");
        }

        List<Utente> tuttiIMembri = new java.util.ArrayList<>();
        if (team.getCapo() != null) {
            tuttiIMembri.add(team.getCapo());
        }
        if (team.getMembri() != null) {
            tuttiIMembri.addAll(team.getMembri());
        }

        return tuttiIMembri.stream()
                .map(this::convertToUtenteDTO)
                .collect(Collectors.toList());
    }

    /**
     * Ottiene le informazioni di un team.
     * @param teamId l'ID del team
     * @param utente l'utente che effettua la richiesta
     * @return le informazioni del team
     * @throws EntityNotFoundException se il team non esiste
     * @throws UnauthorizedException se l'utente non è membro del team
     */
    public TeamResponseDTO ottieniTeam(Long teamId, Utente utente) {
        Optional<Team> teamOpt = teamRepository.findByIdWithCapoAndMembri(teamId);
        if (teamOpt.isEmpty()) {
            throw new EntityNotFoundException("Team non trovato con ID: " + teamId);
        }

        Team team = teamOpt.get();
        if (!team.contieneUtente(utente)) {
            throw new UnauthorizedException("L'utente non è membro del team");
        }

        return convertToTeamResponseDTO(team);
    }

    /**
     * Permette a un utente di abbandonare un team.
     * @param teamId l'ID del team
     * @param utente l'utente che vuole abbandonare il team
     * @throws EntityNotFoundException se il team non esiste
     * @throws UnauthorizedException se l'utente non è membro del team
     */
    public void abbandonaTeam(Long teamId, Utente utente) {
        Optional<Team> teamOpt = teamRepository.findByIdWithCapoAndMembri(teamId);
        if (teamOpt.isEmpty()) {
            throw new EntityNotFoundException("Team non trovato con ID: " + teamId);
        }

        Team team = teamOpt.get();
        if (!team.contieneUtente(utente)) {
            throw new UnauthorizedException("L'utente non è membro del team");
        }

        // Non permettere al capo di abbandonare il team
        if (team.isCapo(utente)) {
            throw new UnauthorizedException("Il capo del team non può abbandonare il team");
        }

        // Rimuovi l'utente dalla lista dei membri
        if (team.getMembri() != null) {
            team.getMembri().removeIf(membro -> membro.getId().equals(utente.getId()));
        }

        teamRepository.save(team);
    }

    /**
     * Iscrive un team a un hackathon (Use case: Iscrive team ad Hackathon).
     * Verifica: finestra iscrizioni, stato IN_ISCRIZIONE, team non già iscritto, limite membri.
     */
    public void iscriviAdHackathon(Long teamId, Long hackathonId) {
        if (hackathonRepository == null || iscrizioneTeamHackathonRepository == null) {
            throw new IllegalStateException("HackathonRepository e IscrizioneTeamHackathonRepository richiesti per iscriviAdHackathon");
        }
        Team team = teamRepository.findByIdWithCapoAndMembri(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team", teamId));
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new EntityNotFoundException("Hackathon", hackathonId));

        LocalDateTime now = LocalDateTime.now();
        if (hackathon.getScadenzaIscrizioni() != null && now.isAfter(hackathon.getScadenzaIscrizioni())) {
            throw new it.hackhub.application.exceptions.core.BusinessLogicException(
                    "Le iscrizioni sono chiuse. Scadenza: " + hackathon.getScadenzaIscrizioni());
        }
        if (hackathon.getInizioIscrizioni() != null && now.isBefore(hackathon.getInizioIscrizioni())) {
            throw new it.hackhub.application.exceptions.core.BusinessLogicException(
                    "Le iscrizioni non sono ancora aperte. Inizio: " + hackathon.getInizioIscrizioni());
        }
        if (hackathon.getStato() != StatoHackathon.IN_ISCRIZIONE) {
            throw new it.hackhub.application.exceptions.core.BusinessLogicException(
                    "L'hackathon non è in fase di iscrizione. Stato: " + hackathon.getStato());
        }
        if (iscrizioneTeamHackathonRepository.findByTeamIdAndHackathonId(teamId, hackathonId).isPresent()) {
            throw new it.hackhub.application.exceptions.core.BusinessLogicException("Il team è già iscritto a questo hackathon.");
        }
        int membriSize = team.getMembri() != null ? team.getMembri().size() : 0;
        int currentTeamSize = membriSize + (team.getCapo() != null ? 1 : 0);
        if (hackathon.getMaxTeamSize() != null && currentTeamSize > hackathon.getMaxTeamSize()) {
            throw new it.hackhub.application.exceptions.core.BusinessLogicException(
                    "Il team supera il numero massimo di membri consentito (" + hackathon.getMaxTeamSize() + ").");
        }

        IscrizioneTeamHackathon iscrizione = new IscrizioneTeamHackathon();
        iscrizione.setTeamId(teamId);
        iscrizione.setHackathonId(hackathonId);
        iscrizione.setDataIscrizione(now);
        iscrizioneTeamHackathonRepository.save(iscrizione);
    }

    /**
     * Nomina un nuovo capo del team. Il nuovo capo deve essere membro del team; il vecchio capo diventa membro.
     */
    public Team nominaNuovoCapo(Long teamId, Long nuovoCapoId) {
        if (utenteRepository == null) {
            throw new IllegalStateException("UtenteRepository richiesto per nominaNuovoCapo");
        }
        Team team = teamRepository.findByIdWithCapoAndMembri(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team", teamId));
        Utente nuovoCapo = utenteRepository.findById(nuovoCapoId)
                .orElseThrow(() -> new EntityNotFoundException("Utente", nuovoCapoId));

        boolean isMembro = team.getMembri() != null
                && team.getMembri().stream().anyMatch(m -> m.getId().equals(nuovoCapoId));
        if (!isMembro) {
            throw new it.hackhub.application.exceptions.core.BusinessLogicException(
                    "Il nuovo capo deve essere un membro attuale del team.");
        }
        Utente vecchioCapo = team.getCapo();
        team.getMembri().removeIf(m -> m.getId().equals(nuovoCapoId));
        if (vecchioCapo != null) {
            team.getMembri().add(vecchioCapo);
        }
        team.setCapo(nuovoCapo);
        return teamRepository.save(team);
    }

    /**
     * Rimuove un membro dal team (solo capo può farlo; non si può rimuovere il capo).
     */
    public Team rimuoviMembro(Long teamId, Long userId) {
        Team team = teamRepository.findByIdWithCapoAndMembri(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team", teamId));
        if (team.getCapo() != null && team.getCapo().getId().equals(userId)) {
            throw new it.hackhub.application.exceptions.core.BusinessLogicException(
                    "Non si può rimuovere il capo del team. Nominare prima un nuovo capo.");
        }
        if (team.getMembri() != null) {
            team.getMembri().removeIf(u -> u.getId().equals(userId));
        }
        return teamRepository.save(team);
    }

    private TeamResponseDTO convertToTeamResponseDTO(Team team) {
        UtenteDTO capoDTO = team.getCapo() != null ? convertToUtenteDTO(team.getCapo()) : null;
        List<UtenteDTO> membriDTO = team.getMembri() != null ? 
                team.getMembri().stream()
                        .map(this::convertToUtenteDTO)
                        .collect(Collectors.toList()) : 
                List.of();

        return new TeamResponseDTO(team.getId(), team.getNome(), team.getEmailPaypal(), capoDTO, membriDTO);
    }

    private UtenteDTO convertToUtenteDTO(Utente utente) {
        return new UtenteDTO(utente.getId(), utente.getNome(), utente.getCognome(), utente.getEmail());
    }
}
