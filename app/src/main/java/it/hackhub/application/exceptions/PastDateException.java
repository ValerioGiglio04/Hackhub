package it.hackhub.application.exceptions;

/**
 * Eccezione lanciata quando si tenta di creare un evento in una data/ora nel passato.
 */
public class PastDateException extends RuntimeException {

  public PastDateException(String message) {
    super(message);
  }

  public PastDateException(String message, Throwable cause) {
    super(message, cause);
  }
}
