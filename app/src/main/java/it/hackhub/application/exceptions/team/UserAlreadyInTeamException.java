package it.hackhub.application.exceptions.team;

import it.hackhub.application.exceptions.core.BusinessLogicException;

/**
 * L'utente appartiene già a un team. Un utente può appartenere a un solo team.
 */
public class UserAlreadyInTeamException extends BusinessLogicException {

  public UserAlreadyInTeamException(String message) {
    super(message);
  }

  public UserAlreadyInTeamException(Long userId) {
    super(
      "L'utente con id " + userId +
      " appartiene già a un team. Un utente può appartenere a un solo team alla volta."
    );
  }
}
