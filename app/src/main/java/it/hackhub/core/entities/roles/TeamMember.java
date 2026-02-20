package it.hackhub.core.entities.roles;

import it.hackhub.core.entities.associations.MembroTeam;

/**
 * Interfaccia che rappresenta la specializzazione "Membro del Team"
 * di "Membro del Team" secondo il diagramma UML Use Case.
 *
 * Il Membro del Team può:
 * - Creare Team
 * - Iscriversi all'hackathon
 * - Vedere membri del team
 * - Abbandonare il team
 */
public interface TeamMember extends MembroTeam {
  /**
   * Verifica se questo è un membro del team (non capo)
   * @return true se è un membro normale (non capo)
   */
  default boolean isMembroNormale() {
    return !isCapoTeam();
  }
}
