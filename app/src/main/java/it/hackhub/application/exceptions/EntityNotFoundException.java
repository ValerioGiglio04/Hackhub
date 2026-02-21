package it.hackhub.application.exceptions;

/**
 * Eccezione quando un'entità non è trovata (compatibile con import da questo package).
 */
public class EntityNotFoundException extends RuntimeException {

  public EntityNotFoundException(String message) {
    super(message);
  }

  public EntityNotFoundException(String entityName, Long id) {
    super(entityName + " non trovato con id: " + id);
  }
}
