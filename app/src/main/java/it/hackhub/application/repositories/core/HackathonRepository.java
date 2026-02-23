package it.hackhub.application.repositories.core;

import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.StatoHackathon;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
  List<Hackathon> findByStato(StatoHackathon stato);

  List<Hackathon> findByStatoNotIn(Collection<StatoHackathon> stati);
}
