package it.hackhub.application.repositories.core;

import it.hackhub.core.entities.core.Valutazione;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValutazioneRepository extends JpaRepository<Valutazione, Long> {
  java.util.List<Valutazione> findBySottomissioneId(Long sottomissioneId);
  Optional<Valutazione> findBySottomissioneIdAndGiudiceId(Long sottomissioneId, Long giudiceId);
}
