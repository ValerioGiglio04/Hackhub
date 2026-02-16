package it.hackhub.infrastructure.persistence.impl;

import it.hackhub.application.repositories.support.SegnalazioneViolazioneRepository;
import it.hackhub.core.entities.support.SegnalazioneViolazione;
import it.hackhub.infrastructure.persistence.StorageInMemoria;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SegnalazioneViolazioneRepositoryImpl implements SegnalazioneViolazioneRepository {

  private final StorageInMemoria storage;

  public SegnalazioneViolazioneRepositoryImpl(StorageInMemoria storage) {
    this.storage = storage;
  }

  @Override
  public SegnalazioneViolazione save(SegnalazioneViolazione segnalazione) {
    if (segnalazione.getId() == null) {
      segnalazione.setId(storage.nextSegnalazioneViolazioneId());
    }
    storage.getSegnalazioniViolazione().put(segnalazione.getId(), segnalazione);
    return segnalazione;
  }

  @Override
  public Optional<SegnalazioneViolazione> findById(Long id) {
    return Optional.ofNullable(storage.getSegnalazioniViolazione().get(id));
  }

  @Override
  public List<SegnalazioneViolazione> findAll() {
    return new ArrayList<>(storage.getSegnalazioniViolazione().values());
  }
}
