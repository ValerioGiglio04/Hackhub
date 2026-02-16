package it.hackhub.application.exceptions.submission;

public class NotAllSubmissionsEvaluatedException extends RuntimeException {

  public NotAllSubmissionsEvaluatedException(Long hackathonId) {
    super("Non tutte le sottomissioni dell'hackathon " + hackathonId + " sono state valutate");
  }
}
