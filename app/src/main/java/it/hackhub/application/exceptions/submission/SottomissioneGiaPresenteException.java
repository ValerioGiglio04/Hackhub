package it.hackhub.application.exceptions.submission;

import it.hackhub.application.exceptions.core.BusinessLogicException;

/**
 * Eccezione lanciata quando un team tenta di inviare una seconda sottomissione
 * per lo stesso hackathon (è ammessa una sola sottomissione per team per hackathon).
 */
public class SottomissioneGiaPresenteException extends BusinessLogicException {

  public SottomissioneGiaPresenteException(String message) {
    super(message);
  }

  public SottomissioneGiaPresenteException(Long teamId, Long hackathonId) {
    super(
      "Il team ha già una sottomissione per questo hackathon. Usare l'endpoint di aggiornamento."
    );
  }
}
