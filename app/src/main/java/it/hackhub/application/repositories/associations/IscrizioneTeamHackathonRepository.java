package it.hackhub.application.repositories.associations;

import it.hackhub.core.entities.associations.IscrizioneTeamHackathon;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IscrizioneTeamHackathonRepository extends JpaRepository<IscrizioneTeamHackathon, Long> {
  @Query("SELECT i FROM IscrizioneTeamHackathon i WHERE i.team.id = :teamId AND i.hackathon.id = :hackathonId")
  Optional<IscrizioneTeamHackathon> findByTeamIdAndHackathonId(@Param("teamId") Long teamId, @Param("hackathonId") Long hackathonId);

  @Query("SELECT i FROM IscrizioneTeamHackathon i WHERE i.hackathon.id = :hackathonId")
  List<IscrizioneTeamHackathon> findByHackathonId(@Param("hackathonId") Long hackathonId);

  @Query("SELECT COUNT(i) FROM IscrizioneTeamHackathon i WHERE i.hackathon.id = :hackathonId")
  long countByHackathonId(@Param("hackathonId") Long hackathonId);
}
