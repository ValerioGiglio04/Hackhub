package it.hackhub.application.repositories.support;

import it.hackhub.core.entities.support.RichiestaSupporto;
import java.util.List;
import java.util.Optional;

public interface RichiestaSupportoRepository {

  RichiestaSupporto save(RichiestaSupporto richiesta);
  Optional<RichiestaSupporto> findById(Long id);
  List<RichiestaSupporto> findAll();
  List<RichiestaSupporto> findByHackathonId(Long hackathonId);
  List<RichiestaSupporto> findByTeamId(Long teamId);
}
