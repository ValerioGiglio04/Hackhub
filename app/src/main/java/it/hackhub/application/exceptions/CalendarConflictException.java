package it.hackhub.application.exceptions;

/**
 * Eccezione lanciata quando si tenta di creare un evento in uno slot temporale
 * che è in conflitto con eventi esistenti nel range di un'ora prima o dopo.
 */
public class CalendarConflictException extends RuntimeException {

  public CalendarConflictException(String message) {
    super(message);
  }

  public CalendarConflictException(String message, Throwable cause) {
    super(message, cause);
  }
}
