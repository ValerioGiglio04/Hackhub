package it.hackhub.infrastructure.persistence.impl;

import it.hackhub.application.repositories.core.ValutazioneRepository;
import it.hackhub.core.entities.core.Valutazione;
import it.hackhub.infrastructure.persistence.StorageInMemoria;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ValutazioneRepositoryImpl implements ValutazioneRepository {

  private final StorageInMemoria storage;

  public ValutazioneRepositoryImpl(StorageInMemoria storage) {
    this.storage = storage;
  }

  @Override
  public Valutazione save(Valutazione valutazione) {
    if (valutazione.getId() == null) {
      valutazione.setId(storage.nextValutazioneId());
    }
    storage.getValutazioni().put(valutazione.getId(), valutazione);
    return valutazione;
  }

  @Override
  public Optional<Valutazione> findById(Long id) {
    return Optional.ofNullable(storage.getValutazioni().get(id));
  }

  @Override
  public List<Valutazione> findBySottomissioneId(Long sottomissioneId) {
    return storage.getValutazioni().values().stream()
        .filter(v -> sottomissioneId.equals(v.getSottomissioneId()))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Valutazione> findBySottomissioneIdAndGiudiceId(Long sottomissioneId, Long giudiceId) {
    return storage.getValutazioni().values().stream()
        .filter(v -> sottomissioneId.equals(v.getSottomissioneId()) && giudiceId.equals(v.getGiudiceId()))
        .findFirst();
  }
}
