package it.hackhub.core.entities.roles;

import it.hackhub.core.entities.associations.MembroTeam;

/**
 * Interfaccia che rappresenta la specializzazione "Capo del Team"
 * di "Membro del Team" secondo il diagramma UML Use Case.
 *
 * Il Capo del Team può:
 * - Iscriversi all'hackathon
 * - Aggiungere membri al Team
 * - Nomina capo del team
 * - Prenotare una call
 * - Visualizzare prenotazioni call
 * - Gestire le call
 * - Pagare il premio al team vincitore
 */
public interface TeamLeader extends MembroTeam {
  /**
   * Verifica se questo è il capo del team
   * @return true se è il capo del team
   */
  default boolean isCapo() {
    return isCapoTeam();
  }
}
