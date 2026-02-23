package it.hackhub.application.exceptions.submission;

import it.hackhub.application.exceptions.core.BusinessLogicException;
import java.time.LocalDateTime;

public class SubmissionDeadlinePassedException extends BusinessLogicException {

  public SubmissionDeadlinePassedException(String message) {
    super(message);
  }

  public SubmissionDeadlinePassedException(LocalDateTime scadenza) {
    super("La scadenza per le sottomissioni è già passata (" + scadenza + ").");
  }
}
