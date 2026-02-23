package it.hackhub.application.exceptions.validation;

import it.hackhub.application.exceptions.core.ValidationException;

public class InvalidGitHubLinkException extends ValidationException {

  public InvalidGitHubLinkException(String message) {
    super(message);
  }
}
