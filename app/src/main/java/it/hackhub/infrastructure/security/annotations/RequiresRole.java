package it.hackhub.infrastructure.security.annotations;

import it.hackhub.core.entities.core.Utente;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotazione per controllare i ruoli richiesti per eseguire un endpoint (RBAC).
 *
 * Questa annotazione ha effetti runtime e viene processata da RequiresRoleAspect
 * per eseguire i controlli di autorizzazione effettivi basati sul token JWT dell'utente.
 *
 * Il sistema verifica automaticamente:
 * - Il token JWT dell'utente autenticato
 * - Il ruolo staff base richiesto (AUTENTICATO, ORGANIZZATORE, MENTORE, GIUDICE)
 * - Requisiti aggiuntivi opzionali (assegnazione hackathon, membership team, leadership)
 *
 * Esempi di utilizzo:
 * - @RequiresRole(Utente.RuoloStaff.ORGANIZZATORE) - solo organizzatori
 * - @RequiresRole(Utente.RuoloStaff.GIUDICE, requiresHackathonAssignment = true) - giudici assegnati all'hackathon
 * - @RequiresRole(Utente.RuoloStaff.MENTORE, requiresHackathonAssignment = true) - mentori assegnati all'hackathon
 * - @RequiresRole(Utente.RuoloStaff.AUTENTICATO) - qualsiasi utente autenticato (utente normale)
 * - @RequiresRole(Utente.RuoloStaff.AUTENTICATO, requiresTeamMember = true) - membri di un team
 * - @RequiresRole(Utente.RuoloStaff.AUTENTICATO, requiresTeamLeader = true) - capo di un team
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {

  /** Ruolo staff richiesto (AUTENTICATO, ORGANIZZATORE, MENTORE, GIUDICE). */
  @Enumerated(EnumType.STRING)
  Utente.RuoloStaff role() default Utente.RuoloStaff.AUTENTICATO;

  /** Se true, richiede che lo staff sia assegnato all'hackathon specificato. */
  boolean requiresHackathonAssignment() default false;

  /** Se true, richiede che l'utente sia membro di un team (capo o membro). */
  boolean requiresTeamMember() default false;

  /** Se true, richiede che l'utente sia il capo (team leader) del team. */
  boolean requiresTeamLeader() default false;

  /** Descrizione opzionale del requisito di autorizzazione. */
  String description() default "";
}
