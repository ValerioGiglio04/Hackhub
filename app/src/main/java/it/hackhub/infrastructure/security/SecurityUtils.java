package it.hackhub.infrastructure.security;

import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Utente;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

  private SecurityUtils() {}

  /**
   * Restituisce l'id dell'utente autenticato (email dal token → Utente → id).
   */
  public static Long getCurrentUserId(UtenteRepository utenteRepository) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
      throw new UnauthorizedException("Utente non autenticato");
    }
    Utente utente = utenteRepository
      .findByEmail(auth.getName())
      .orElseThrow(() -> new UnauthorizedException("Utente non trovato"));
    return utente.getId();
  }
}
