package it.hackhub.application.repositories.core;

import it.hackhub.core.entities.core.Utente;
import java.util.List;
import java.util.Optional;

public interface UtenteRepository {

  Utente save(Utente utente);
  Optional<Utente> findById(Long id);
  List<Utente> findAll();
  Optional<Utente> findByEmail(String email);
}
