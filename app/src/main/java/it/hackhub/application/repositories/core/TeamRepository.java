package it.hackhub.application.repositories.core;

import it.hackhub.core.entities.core.Team;
import java.util.List;
import java.util.Optional;

public interface TeamRepository {

  Team save(Team team);
  Optional<Team> findById(Long id);
  List<Team> findAll();
  Optional<Team> findByIdWithCapoAndMembri(Long id);
  Optional<Team> findByMembroOrCapoId(Long utenteId);
  int countTeamsIscritti(Long hackathonId);
  List<Team> findTeamsIscritti(Long hackathonId);
  void iscriviTeam(Long hackathonId, Long teamId);
  boolean isTeamIscritto(Long hackathonId, Long teamId);
}
