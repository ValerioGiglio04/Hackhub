package it.hackhub.core.entities.roles;

import it.hackhub.core.entities.core.Utente;

/**
 * Interfaccia che rappresenta la generalizzazione "Membro dello Staff"
 * secondo il diagramma UML Use Case.
 * Specializzazioni: Mentore, Giudice (e Organizzatore a livello applicativo).
 */
public interface MembroStaff {

  Utente getUtente();

  Utente.RuoloStaff getRuolo();

  boolean hasRuolo(Utente.RuoloStaff ruolo);
}
