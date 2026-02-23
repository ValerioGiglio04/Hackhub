package it.hackhub.application.repositories.core;

import it.hackhub.core.entities.core.Utente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {
  Optional<Utente> findByEmail(String email);
}
