package it.hackhub.application.repositories;

import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;

import java.util.List;
import java.util.Optional;

/**
 * Repository per la gestione delle operazioni CRUD e query personalizzate
 * per l'entità Team.
 */
public interface TeamRepository {

    /**
     * Salva un team nel database.
     * @param team il team da salvare
     * @return il team salvato con l'ID assegnato
     */
    Team save(Team team);

    /**
     * Trova un team tramite il suo ID.
     * @param id l'ID del team
     * @return un Optional contenente il team se trovato
     */
    Optional<Team> findById(Long id);

    /**
     * Recupera tutti i team.
     * @return lista di tutti i team
     */
    List<Team> findAll();

    /**
     * Recupera tutti i team con il capo caricato.
     * @return lista di team con il capo popolato
     */
    List<Team> findAllWithCapo();

    /**
     * Recupera tutti i team con capo e membri caricati.
     * @return lista di team con capo e membri popolati
     */
    List<Team> findAllWithCapoAndMembri();

    /**
     * Trova un team tramite ID con il capo caricato.
     * @param id l'ID del team
     * @return un Optional contenente il team con capo se trovato
     */
    Optional<Team> findByIdWithCapo(Long id);

    /**
     * Trova un team tramite ID con capo e membri caricati.
     * @param id l'ID del team
     * @return un Optional contenente il team con capo e membri se trovato
     */
    Optional<Team> findByIdWithCapoAndMembri(Long id);

    /**
     * Trova un team di cui un utente è capo o membro.
     * @param utenteId l'ID dell'utente
     * @return un Optional contenente il team se trovato
     */
    Optional<Team> findByMembroOrCapoId(Long utenteId);

    /**
     * Elimina un team tramite ID.
     * @param id l'ID del team da eliminare
     */
    void deleteById(Long id);

    /**
     * Verifica se esiste un team con l'ID specificato.
     * @param id l'ID del team
     * @return true se il team esiste
     */
    boolean existsById(Long id);
}
