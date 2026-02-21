package it.hackhub.infrastructure.persistence.impl;

import it.hackhub.application.repositories.associations.IscrizioneTeamHackathonRepository;
import it.hackhub.core.entities.associations.IscrizioneTeamHackathon;
import it.hackhub.infrastructure.persistence.StorageInMemoria;
import java.util.Optional;

public class IscrizioneTeamHackathonRepositoryImpl implements IscrizioneTeamHackathonRepository {

  private final StorageInMemoria storage;

  public IscrizioneTeamHackathonRepositoryImpl(StorageInMemoria storage) {
    this.storage = storage;
  }

  @Override
  public IscrizioneTeamHackathon save(IscrizioneTeamHackathon iscrizione) {
    if (iscrizione.getId() == null) {
      iscrizione.setId(storage.nextIscrizioneTeamHackathonId());
    }
    storage.getIscrizioniTeamHackathon().put(iscrizione.getId(), iscrizione);
    return iscrizione;
  }

  @Override
  public Optional<IscrizioneTeamHackathon> findByTeamIdAndHackathonId(Long teamId, Long hackathonId) {
    return storage.getIscrizioniTeamHackathon().values().stream()
        .filter(i -> teamId != null && teamId.equals(i.getTeamId()))
        .filter(i -> hackathonId != null && hackathonId.equals(i.getHackathonId()))
        .findFirst();
  }
}
