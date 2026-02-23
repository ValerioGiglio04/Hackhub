package it.hackhub.application.exceptions.hackathon;

import it.hackhub.application.exceptions.core.BusinessLogicException;

public class HackathonNotInRegistrationPhaseException
  extends BusinessLogicException {

  public HackathonNotInRegistrationPhaseException(String message) {
    super(message);
  }

  public HackathonNotInRegistrationPhaseException(Long hackathonId) {
    super(
      "L'hackathon con id " + hackathonId + " non è nella fase di iscrizione."
    );
  }
}
