package it.hackhub.application.repositories.associations;

import it.hackhub.core.entities.associations.InvitoTeam;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitoTeamRepository extends JpaRepository<InvitoTeam, Long> {

  List<InvitoTeam> findByUtenteInvitatoIdAndStato(
    Long utenteInvitatoId,
    InvitoTeam.StatoInvito stato
  );

  List<InvitoTeam> findByTeamIdAndStato(Long teamId, InvitoTeam.StatoInvito stato);

  Optional<InvitoTeam> findByTeamIdAndUtenteInvitatoIdAndStato(
    Long teamId,
    Long utenteInvitatoId,
    InvitoTeam.StatoInvito stato
  );

  @EntityGraph(attributePaths = { "team", "team.capo", "team.membri", "utenteInvitato" })
  @Query("SELECT i FROM InvitoTeam i WHERE i.id = :id")
  Optional<InvitoTeam> findByIdWithDetails(@Param("id") Long id);
}
