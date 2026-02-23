package it.hackhub.core.entities.roles;

import it.hackhub.core.entities.core.Utente;

/**
 * Interfaccia che rappresenta la specializzazione "Mentore"
 * di "Membro dello Staff" secondo il diagramma UML Use Case.
 * Il Mentore può proporre chiamate, accedere alle richieste di supporto, segnalare violazioni.
 */
public interface Mentore extends MembroStaff {

  default boolean isMentore() {
    return hasRuolo(Utente.RuoloStaff.MENTORE);
  }
}
