package it.hackhub.infrastructure.persistence.impl;

import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.infrastructure.persistence.StorageInMemoria;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtenteRepositoryImpl implements UtenteRepository {

  private final StorageInMemoria storage;

  public UtenteRepositoryImpl(StorageInMemoria storage) {
    this.storage = storage;
  }

  @Override
  public Utente save(Utente utente) {
    if (utente.getId() == null) {
      utente.setId(storage.nextUtenteId());
    }
    storage.getUtenti().put(utente.getId(), utente);
    return utente;
  }

  @Override
  public Optional<Utente> findById(Long id) {
    return Optional.ofNullable(storage.getUtenti().get(id));
  }

  @Override
  public List<Utente> findAll() {
    return new ArrayList<>(storage.getUtenti().values());
  }

  @Override
  public Optional<Utente> findByEmail(String email) {
    return storage.getUtenti().values().stream()
        .filter(u -> email != null && email.equals(u.getEmail()))
        .findFirst();
  }
}
