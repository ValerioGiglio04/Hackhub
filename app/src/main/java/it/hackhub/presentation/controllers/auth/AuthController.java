package it.hackhub.presentation.controllers.auth;

import it.hackhub.application.dto.auth.LoginDTO;
import it.hackhub.application.dto.auth.RegistrazioneDTO;
import it.hackhub.application.dto.utente.UtenteResponseDTO;
import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.exceptions.core.ValidationException;
import it.hackhub.application.handlers.auth.AuthHandler;
import it.hackhub.application.mappers.UtenteDtoMapper;
import it.hackhub.core.entities.core.Utente;
import java.util.regex.Pattern;

/**
 * Controller per autenticazione: login e registrazione.
 * POST /api/autenticazione/login, POST /api/autenticazione/registrazione.
 * Nessun riferimento a Spring Boot: validazione manuale, eccezioni per 400/401.
 */
public class AuthController {

  private static final Pattern EMAIL_PATTERN = Pattern.compile(
    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
  );
  private static final int MIN_PASSWORD_LENGTH = 6;

  private final AuthHandler authHandler;

  public AuthController(AuthHandler authHandler) {
    this.authHandler = authHandler;
  }

  /**
   * Login: validazione LoginDTO, poi AuthHandler.login.
   * Success 200 → token JWT; Error 400 → dati non validi; Error 401 → credenziali non valide.
   */
  public String login(LoginDTO loginRequest) {
    validateLogin(loginRequest);
    return authHandler.login(loginRequest.getEmail(), loginRequest.getPassword());
  }

  /**
   * Registrazione: validazione RegistrazioneDTO (campi non vuoti, email valida, password lunghezza minima, nome/cognome validi).
   * Se validazione fallita → ValidationException (400). Se email già esistente → BusinessLogicException (400).
   * Success 200 → UtenteResponseDTO.
   */
  public UtenteResponseDTO registrazione(RegistrazioneDTO dto) {
    validateRegistrazione(dto);
    Utente utente = UtenteDtoMapper.toEntity(dto);
    Utente creato = authHandler.registrazione(utente);
    return UtenteDtoMapper.toResponseDTO(creato);
  }

  private void validateLogin(LoginDTO dto) {
    if (dto == null) {
      throw new ValidationException("Dati di login obbligatori");
    }
    if (dto.getEmail() == null || dto.getEmail().isBlank()) {
      throw new ValidationException("L'email è obbligatoria");
    }
    if (dto.getPassword() == null || dto.getPassword().isBlank()) {
      throw new ValidationException("La password è obbligatoria");
    }
  }

  private void validateRegistrazione(RegistrazioneDTO dto) {
    if (dto == null) {
      throw new ValidationException("Dati di registrazione obbligatori");
    }
    if (dto.getEmail() == null || dto.getEmail().isBlank()) {
      throw new ValidationException("L'email è obbligatoria");
    }
    if (!EMAIL_PATTERN.matcher(dto.getEmail().trim()).matches()) {
      throw new ValidationException("L'email non è valida");
    }
    if (dto.getPassword() == null || dto.getPassword().length() < MIN_PASSWORD_LENGTH) {
      throw new ValidationException("La password deve essere di almeno " + MIN_PASSWORD_LENGTH + " caratteri");
    }
    if (dto.getNome() == null || dto.getNome().isBlank()) {
      throw new ValidationException("Il nome è obbligatorio");
    }
    if (dto.getCognome() == null || dto.getCognome().isBlank()) {
      throw new ValidationException("Il cognome è obbligatorio");
    }
    if (dto.getRuolo() != null && !dto.getRuolo().isBlank()) {
      String r = dto.getRuolo().trim().toUpperCase();
      try {
        Utente.RuoloStaff.valueOf(r);
      } catch (IllegalArgumentException e) {
        throw new ValidationException("Ruolo non valido. Valori ammessi: AUTENTICATO, ORGANIZZATORE, MENTORE, GIUDICE");
      }
    }
  }
}
