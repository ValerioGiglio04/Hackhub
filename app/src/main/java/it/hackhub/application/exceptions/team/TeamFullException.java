package it.hackhub.application.exceptions.team;

import it.hackhub.application.exceptions.core.BusinessLogicException;

public class TeamFullException extends BusinessLogicException {

  public TeamFullException(String message) {
    super(message);
  }

  public TeamFullException(Long teamId, Integer maxSize) {
    super(
      "Il team con id " +
      teamId +
      " ha raggiunto il numero massimo di membri (" +
      maxSize +
      ")."
    );
  }
}
