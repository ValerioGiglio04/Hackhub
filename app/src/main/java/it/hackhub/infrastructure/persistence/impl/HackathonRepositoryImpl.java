package it.hackhub.infrastructure.persistence.impl;

import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.StatoHackathon;
import it.hackhub.infrastructure.persistence.StorageInMemoria;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HackathonRepositoryImpl implements HackathonRepository {

  private final StorageInMemoria storage;

  public HackathonRepositoryImpl(StorageInMemoria storage) {
    this.storage = storage;
  }

  @Override
  public Hackathon save(Hackathon hackathon) {
    if (hackathon.getId() == null) {
      hackathon.setId(storage.nextHackathonId());
    }
    storage.getHackathons().put(hackathon.getId(), hackathon);
    return hackathon;
  }

  @Override
  public Optional<Hackathon> findById(Long id) {
    return Optional.ofNullable(storage.getHackathons().get(id));
  }

  @Override
  public List<Hackathon> findAll() {
    return new ArrayList<>(storage.getHackathons().values());
  }

  @Override
  public List<Hackathon> findByStato(StatoHackathon stato) {
    return storage
      .getHackathons()
      .values()
      .stream()
      .filter(h -> h.getStato() == stato)
      .collect(Collectors.toList());
  }
}
