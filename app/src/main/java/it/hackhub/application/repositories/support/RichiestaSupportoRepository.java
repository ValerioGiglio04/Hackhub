package it.hackhub.application.repositories.support;

import it.hackhub.core.entities.support.RichiestaSupporto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RichiestaSupportoRepository extends JpaRepository<RichiestaSupporto, Long> {
  List<RichiestaSupporto> findByHackathonId(Long hackathonId);
  List<RichiestaSupporto> findByTeamId(Long teamId);
}
