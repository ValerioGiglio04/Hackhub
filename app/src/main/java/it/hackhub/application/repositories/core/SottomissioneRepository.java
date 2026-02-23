package it.hackhub.application.repositories.core;

import it.hackhub.core.entities.core.Sottomissione;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SottomissioneRepository extends JpaRepository<Sottomissione, Long> {
  List<Sottomissione> findByHackathonId(Long hackathonId);
  List<Sottomissione> findByTeamId(Long teamId);
}
