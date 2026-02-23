package it.hackhub.infrastructure.security.userdetails;

import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Utente;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UtenteRepository utenteRepository;

  public CustomUserDetailsService(UtenteRepository utenteRepository) {
    this.utenteRepository = utenteRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {
    Utente utente = utenteRepository
      .findByEmail(username)
      .orElseThrow(() ->
        new UsernameNotFoundException(
          "Utente non trovato con email: " + username
        )
      );
    return new CustomUserDetails(utente);
  }
}
