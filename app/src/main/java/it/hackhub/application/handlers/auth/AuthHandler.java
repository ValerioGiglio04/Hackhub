package it.hackhub.application.handlers.auth;

import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.infrastructure.security.jwt.JwtTokenProvider;
import it.hackhub.core.entities.core.Utente;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthHandler {

  private final UtenteRepository utenteRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;

  public AuthHandler(
    UtenteRepository utenteRepository,
    PasswordEncoder passwordEncoder,
    AuthenticationManager authenticationManager,
    JwtTokenProvider jwtTokenProvider
  ) {
    this.utenteRepository = utenteRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  public String login(String email, String password) {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(email, password)
    );
    return jwtTokenProvider.generateToken(authentication);
  }

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
