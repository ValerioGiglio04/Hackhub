package it.hackhub.infrastructure.persistence.impl;

import it.hackhub.application.repositories.associations.StaffHackatonRepository;
import it.hackhub.core.entities.associations.StaffHackaton;
import it.hackhub.infrastructure.persistence.StorageInMemoria;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StaffHackatonRepositoryImpl implements StaffHackatonRepository {

  private final StorageInMemoria storage;

  public StaffHackatonRepositoryImpl(StorageInMemoria storage) {
    this.storage = storage;
  }

  @Override
  public StaffHackaton save(StaffHackaton sh) {
    if (sh.getId() == null) {
      sh.setId(storage.nextStaffHackatonId());
    }
    storage.getStaffHackaton().put(sh.getId(), sh);
    return sh;
  }

  @Override
  public Optional<StaffHackaton> findById(Long id) {
    return Optional.ofNullable(storage.getStaffHackaton().get(id));
  }

  @Override
  public List<StaffHackaton> findByHackathonId(Long hackathonId) {
    return storage.getStaffHackaton().values().stream()
        .filter(sh -> hackathonId != null && sh.getHackathon() != null && hackathonId.equals(sh.getHackathon().getId()))
        .collect(Collectors.toList());
  }

  @Override
  public List<StaffHackaton> findByUtenteId(Long utenteId) {
    return storage.getStaffHackaton().values().stream()
        .filter(sh -> utenteId != null && sh.getUtente() != null && utenteId.equals(sh.getUtente().getId()))
        .collect(Collectors.toList());
  }
}
