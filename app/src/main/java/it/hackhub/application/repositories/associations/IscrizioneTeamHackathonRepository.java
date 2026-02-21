package it.hackhub.application.repositories.associations;

import it.hackhub.core.entities.associations.IscrizioneTeamHackathon;
import java.util.Optional;

public interface IscrizioneTeamHackathonRepository {

  IscrizioneTeamHackathon save(IscrizioneTeamHackathon iscrizione);
  Optional<IscrizioneTeamHackathon> findByTeamIdAndHackathonId(Long teamId, Long hackathonId);
}
