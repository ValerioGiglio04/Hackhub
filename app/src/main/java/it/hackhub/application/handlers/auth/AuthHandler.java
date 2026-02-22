package it.hackhub.application.handlers.auth;

import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.application.security.PasswordEncoder;
import it.hackhub.application.security.jwt.JwtTokenProvider;
import it.hackhub.core.entities.core.Utente;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Handler per login e registrazione (senza Spring).
 * Login: verifica credenziali tramite UtenteRepository e PasswordEncoder, genera JWT.
 * Registrazione: verifica email non esistente, codifica password, setDataRegistrazione, save.
 */
public class AuthHandler {

  private final UtenteRepository utenteRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  public AuthHandler(
    UtenteRepository utenteRepository,
    PasswordEncoder passwordEncoder,
    JwtTokenProvider jwtTokenProvider
  ) {
    this.utenteRepository = utenteRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  /**
   * Autenticazione: carica utente per email, verifica password, restituisce token JWT.
   *
   * @throws it.hackhub.application.exceptions.UnauthorizedException se credenziali non valide
   */
  public String login(String email, String password) {
    Optional<Utente> opt = utenteRepository.findByEmail(email);
    if (opt.isEmpty()) {
      throw new it.hackhub.application.exceptions.UnauthorizedException("Credenziali non valide");
    }
    Utente utente = opt.get();
    String hash = utente.getPasswordHash();
    if (hash == null || !passwordEncoder.matches(password, hash)) {
      throw new it.hackhub.application.exceptions.UnauthorizedException("Credenziali non valide");
    }
    return jwtTokenProvider.generateToken(utente);
  }

  /**
   * Registrazione: verifica email non esistente, codifica password, setDataRegistrazione(now), save.
   *
   * @throws BusinessLogicException se email già esistente
   */
  public Utente registrazione(Utente utente) {
    Optional<Utente> esistente = utenteRepository.findByEmail(utente.getEmail());
    if (esistente.isPresent()) {
      throw new BusinessLogicException("Email già esistente");
    }
    String plain = utente.getPasswordHash();
    if (plain != null) {
      utente.setPasswordHash(passwordEncoder.encode(plain));
    }
    utente.setDataRegistrazione(LocalDate.now());
    return utenteRepository.save(utente);
  }
}
