package it.hackhub.application.exceptions.core;

public class EntityNotFoundException extends RuntimeException {

  public EntityNotFoundException(String entityName, Long id) {
    super(entityName + " non trovato con id: " + id);
  }
}
