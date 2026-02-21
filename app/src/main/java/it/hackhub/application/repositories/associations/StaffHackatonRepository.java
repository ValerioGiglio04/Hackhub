package it.hackhub.application.repositories.associations;

import it.hackhub.core.entities.associations.StaffHackaton;
import java.util.List;
import java.util.Optional;

public interface StaffHackatonRepository {

  StaffHackaton save(StaffHackaton staffHackaton);
  Optional<StaffHackaton> findById(Long id);
  List<StaffHackaton> findByHackathonId(Long hackathonId);
  List<StaffHackaton> findByUtenteId(Long utenteId);
}
