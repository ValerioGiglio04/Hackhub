package it.hackhub.core.entities.associations;

import it.hackhub.core.entities.core.Utente;
import it.hackhub.core.entities.core.Team;

/**
 * Interfaccia che rappresenta la generalizzazione "Membro del Team"
 * secondo il diagramma UML Use Case.
 *
 * Le specializzazioni sono:
 * - TeamMember (Membro del Team)
 * - TeamLeader (Capo del Team)
 */
public interface MembroTeam {
  /**
   * Ottiene l'utente associato
   * @return l'entità Utente
   */
  Utente getUtente();

  /**
   * Ottiene il team a cui appartiene
   * @return l'entità Team
   */
  Team getTeam();

  /**
   * Verifica se il membro è il capo del team
   * @return true se è il capo del team
   */
  boolean isCapoTeam();
}
