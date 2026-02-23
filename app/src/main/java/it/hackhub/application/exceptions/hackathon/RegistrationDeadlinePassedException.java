package it.hackhub.application.exceptions.hackathon;

import it.hackhub.application.exceptions.core.BusinessLogicException;
import java.time.LocalDateTime;

public class RegistrationDeadlinePassedException extends BusinessLogicException {

  public RegistrationDeadlinePassedException(String message) {
    super(message);
  }

  public RegistrationDeadlinePassedException(LocalDateTime scadenza) {
    super("La scadenza per le iscrizioni è già passata (" + scadenza + ").");
  }
}
