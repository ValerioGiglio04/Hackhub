package it.hackhub.application.repositories.core;

import it.hackhub.core.entities.core.Team;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

  @EntityGraph(attributePaths = { "capo", "membri" })
  @Query("SELECT t FROM Team t WHERE t.id = :id")
  Optional<Team> findByIdWithCapoAndMembri(@Param("id") Long id);

  @Query(
    "SELECT t FROM Team t WHERE t.capo.id = :utenteId OR :utenteId IN (SELECT m.id FROM t.membri m)"
  )
  Optional<Team> findByMembroOrCapoId(@Param("utenteId") Long utenteId);
}
