package it.hackhub.infrastructure.persistence.impl;

import it.hackhub.application.repositories.core.SottomissioneRepository;
import it.hackhub.core.entities.core.Sottomissione;
import it.hackhub.infrastructure.persistence.StorageInMemoria;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SottomissioneRepositoryImpl implements SottomissioneRepository {

  private final StorageInMemoria storage;

  public SottomissioneRepositoryImpl(StorageInMemoria storage) {
    this.storage = storage;
  }

  @Override
  public Sottomissione save(Sottomissione sottomissione) {
    if (sottomissione.getId() == null) {
      sottomissione.setId(storage.nextSottomissioneId());
    }
    storage.getSottomissioni().put(sottomissione.getId(), sottomissione);
    return sottomissione;
  }

  @Override
  public Optional<Sottomissione> findById(Long id) {
    return Optional.ofNullable(storage.getSottomissioni().get(id));
  }

  @Override
  public List<Sottomissione> findAll() {
    return new ArrayList<>(storage.getSottomissioni().values());
  }

  @Override
  public List<Sottomissione> findByHackathonId(Long hackathonId) {
    return storage.getSottomissioni().values().stream()
        .filter(s -> hackathonId.equals(s.getHackathonId()))
        .collect(Collectors.toList());
  }
}
