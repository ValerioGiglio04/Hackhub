package it.hackhub.application.repositories.associations;

import it.hackhub.core.entities.associations.IscrizioneTeamHackathon;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IscrizioneTeamHackathonRepository extends JpaRepository<IscrizioneTeamHackathon, Long> {
  Optional<IscrizioneTeamHackathon> findByTeamIdAndHackathonId(Long teamId, Long hackathonId);
  long countByHackathon_Id(Long hackathonId);
  java.util.List<IscrizioneTeamHackathon> findByHackathonId(Long hackathonId);
}
