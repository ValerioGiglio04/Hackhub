package it.hackhub.application.repositories.associations;

import it.hackhub.core.entities.associations.StaffHackaton;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffHackatonRepository
  extends JpaRepository<StaffHackaton, Long> {
  List<StaffHackaton> findByHackathonId(Long hackathonId);

  @Query("SELECT s FROM StaffHackaton s WHERE s.utente.id = :utenteId")
  List<StaffHackaton> findByUtenteId(Long utenteId);
}
