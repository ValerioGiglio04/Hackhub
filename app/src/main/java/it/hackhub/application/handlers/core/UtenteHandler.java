package it.hackhub.application.handlers.core;

import it.hackhub.core.entities.core.Utente;
import it.hackhub.application.repositories.core.UtenteRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UtenteHandler {

  private final UtenteRepository utenteRepository;

  public UtenteHandler(UtenteRepository utenteRepository) {
    this.utenteRepository = utenteRepository;
  }

  public List<Utente> ottieniTuttiGliUtenti() {
    return utenteRepository.findAll();
  }

  public Optional<Utente> ottieniUtentePerId(Long id) {
    return utenteRepository.findById(id);
  }

  public Optional<Utente> ottieniUtentePerEmail(String email) {
    return utenteRepository.findByEmail(email);
  }

  public Utente creaUtente(Utente utente) {
    return utenteRepository.save(utente);
  }

  public UtenteRepository getUtenteRepository() {
    return utenteRepository;
  }
}
