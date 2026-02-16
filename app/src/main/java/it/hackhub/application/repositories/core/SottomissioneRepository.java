package it.hackhub.application.repositories.core;

import it.hackhub.core.entities.core.Sottomissione;
import java.util.List;
import java.util.Optional;

public interface SottomissioneRepository {

  Sottomissione save(Sottomissione sottomissione);
  Optional<Sottomissione> findById(Long id);
  List<Sottomissione> findAll();
  List<Sottomissione> findByHackathonId(Long hackathonId);
}
