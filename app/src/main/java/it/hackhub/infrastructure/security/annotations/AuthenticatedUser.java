package it.hackhub.infrastructure.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotazione per richiedere semplicemente l'autenticazione dell'utente.
 *
 * Questa annotazione ha effetti runtime e viene processata da RequiresRoleAspect
 * per verificare che l'utente sia autenticato tramite token JWT, senza richiedere
 * ruoli specifici o altri requisiti.
 *
 * A differenza di @RequiresRole, questa annotazione non controlla il ruolo dell'utente
 * ma verifica solo che sia presente un token JWT valido nel contesto di sicurezza.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthenticatedUser {

  /** Descrizione opzionale del requisito di autenticazione. */
  String description() default "Richiede autenticazione";
}
