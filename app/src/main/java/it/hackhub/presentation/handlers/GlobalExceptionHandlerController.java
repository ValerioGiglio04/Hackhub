package it.hackhub.presentation.handlers;

import it.hackhub.application.dto.common.ApiResponseFactory;
import it.hackhub.application.dto.common.StandardResponse;
import it.hackhub.application.exceptions.CalendarConflictException;
import it.hackhub.application.exceptions.PastDateException;
import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.exceptions.core.ValidationException;
import it.hackhub.application.exceptions.hackathon.HackathonNotInRegistrationPhaseException;
import it.hackhub.application.exceptions.hackathon.RegistrationDeadlinePassedException;
import it.hackhub.application.exceptions.submission.NotAllSubmissionsEvaluatedException;
import it.hackhub.application.exceptions.submission.SubmissionDeadlinePassedException;
import it.hackhub.application.exceptions.team.TeamFullException;
import it.hackhub.application.exceptions.team.UserAlreadyInTeamException;
import it.hackhub.application.exceptions.valutazione.ValutazioneGiaEsistenteException;
import it.hackhub.application.exceptions.validation.InvalidGitHubLinkException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlerController {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<StandardResponse<Void>> handleEntityNotFound(
    EntityNotFoundException e
  ) {
    return ApiResponseFactory.error(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(
    {
      BusinessLogicException.class,
      ValidationException.class,
      IllegalArgumentException.class,
      ValutazioneGiaEsistenteException.class,
      HackathonNotInRegistrationPhaseException.class,
      RegistrationDeadlinePassedException.class,
      SubmissionDeadlinePassedException.class,
      TeamFullException.class,
      UserAlreadyInTeamException.class,
      NotAllSubmissionsEvaluatedException.class,
      InvalidGitHubLinkException.class,
    }
  )
  public ResponseEntity<StandardResponse<Void>> handleBadRequest(Exception e) {
    return ApiResponseFactory.error(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<StandardResponse<Void>> handleValidation(
    MethodArgumentNotValidException e
  ) {
    Map<String, String> errors = new HashMap<>();
    e
      .getBindingResult()
      .getAllErrors()
      .forEach(error -> {
        String fieldName = ((FieldError) error).getField();
        String errorMessage = error.getDefaultMessage();
        errors.put(fieldName, errorMessage);
      });
    return ApiResponseFactory.validationError("Errore di validazione", errors);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<StandardResponse<Void>> handleUnauthorized(
    UnauthorizedException e
  ) {
    return ApiResponseFactory.error(HttpStatus.UNAUTHORIZED, e.getMessage());
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<StandardResponse<Void>> handleBadCredentials(
    BadCredentialsException e
  ) {
    return ApiResponseFactory.error(
      HttpStatus.UNAUTHORIZED,
      "Credenziali non valide"
    );
  }

  @ExceptionHandler(CalendarConflictException.class)
  public ResponseEntity<StandardResponse<Void>> handleCalendarConflict(
    CalendarConflictException e
  ) {
    return ApiResponseFactory.error(HttpStatus.CONFLICT, e.getMessage());
  }

  @ExceptionHandler(PastDateException.class)
  public ResponseEntity<StandardResponse<Void>> handlePastDate(
    PastDateException e
  ) {
    return ApiResponseFactory.error(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<StandardResponse<Void>> handleGeneric(Exception e) {
    return ApiResponseFactory.error(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "Errore interno: " + e.getMessage()
    );
  }
}
