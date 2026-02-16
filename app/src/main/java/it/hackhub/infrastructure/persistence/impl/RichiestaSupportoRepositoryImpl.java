package it.hackhub.infrastructure.persistence.impl;

import it.hackhub.application.repositories.support.RichiestaSupportoRepository;
import it.hackhub.core.entities.support.RichiestaSupporto;
import it.hackhub.infrastructure.persistence.StorageInMemoria;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RichiestaSupportoRepositoryImpl implements RichiestaSupportoRepository {

  private final StorageInMemoria storage;

  public RichiestaSupportoRepositoryImpl(StorageInMemoria storage) {
    this.storage = storage;
  }

  @Override
  public RichiestaSupporto save(RichiestaSupporto richiesta) {
    if (richiesta.getId() == null) {
      richiesta.setId(storage.nextRichiestaSupportoId());
    }
    storage.getRichiesteSupporto().put(richiesta.getId(), richiesta);
    return richiesta;
  }

  @Override
  public Optional<RichiestaSupporto> findById(Long id) {
    return Optional.ofNullable(storage.getRichiesteSupporto().get(id));
  }

  @Override
  public List<RichiestaSupporto> findAll() {
    return new ArrayList<>(storage.getRichiesteSupporto().values());
  }

  @Override
  public List<RichiestaSupporto> findByHackathonId(Long hackathonId) {
    return storage.getRichiesteSupporto().values().stream()
        .filter(r -> hackathonId.equals(r.getHackathonId()))
        .collect(Collectors.toList());
  }

  @Override
  public List<RichiestaSupporto> findByTeamId(Long teamId) {
    return storage.getRichiesteSupporto().values().stream()
        .filter(r -> teamId.equals(r.getTeamId()))
        .collect(Collectors.toList());
  }
}
