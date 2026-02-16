package it.hackhub.application.repositories.support;

import it.hackhub.core.entities.support.SegnalazioneViolazione;
import java.util.List;
import java.util.Optional;

public interface SegnalazioneViolazioneRepository {

  SegnalazioneViolazione save(SegnalazioneViolazione segnalazione);
  Optional<SegnalazioneViolazione> findById(Long id);
  List<SegnalazioneViolazione> findAll();
}
