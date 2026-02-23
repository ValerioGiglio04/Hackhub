package it.hackhub.core.entities.roles;

import it.hackhub.core.entities.core.Utente;

/**
 * Interfaccia che rappresenta la specializzazione "Giudice"
 * di "Membro dello Staff" secondo il diagramma UML Use Case.
 * Il Giudice può visualizzare e valutare le sottomissioni.
 */
public interface Giudice extends MembroStaff {

  default boolean isGiudice() {
    return hasRuolo(Utente.RuoloStaff.GIUDICE);
  }
}
