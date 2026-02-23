package it.hackhub.application.repositories.associations;

import it.hackhub.core.entities.associations.InvitoStaff;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitoStaffRepository extends JpaRepository<InvitoStaff, Long> {

  List<InvitoStaff> findByUtenteInvitatoIdAndStato(
    Long utenteInvitatoId,
    InvitoStaff.StatoInvito stato
  );

  List<InvitoStaff> findByHackathonIdAndStato(
    Long hackathonId,
    InvitoStaff.StatoInvito stato
  );

  Optional<InvitoStaff> findByHackathonIdAndUtenteInvitatoIdAndStato(
    Long hackathonId,
    Long utenteInvitatoId,
    InvitoStaff.StatoInvito stato
  );

  @EntityGraph(attributePaths = { "hackathon", "utenteInvitato", "mittente" })
  @Query("SELECT i FROM InvitoStaff i WHERE i.id = :id")
  Optional<InvitoStaff> findByIdWithDetails(@Param("id") Long id);
}
