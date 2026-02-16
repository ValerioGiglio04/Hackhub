package it.hackhub.application.repositories.core;

import it.hackhub.core.entities.core.Valutazione;
import java.util.List;
import java.util.Optional;

public interface ValutazioneRepository {

  Valutazione save(Valutazione valutazione);
  Optional<Valutazione> findById(Long id);
  List<Valutazione> findBySottomissioneId(Long sottomissioneId);
  Optional<Valutazione> findBySottomissioneIdAndGiudiceId(Long sottomissioneId, Long giudiceId);
}
