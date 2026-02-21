package it.hackhub.infrastructure.persistence.impl;

import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.core.entities.core.Team;
import it.hackhub.infrastructure.persistence.StorageInMemoria;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TeamRepositoryImpl implements TeamRepository {

  private final StorageInMemoria storage;

  public TeamRepositoryImpl(StorageInMemoria storage) {
    this.storage = storage;
  }

  @Override
  public Team save(Team team) {
    if (team.getId() == null) {
      team.setId(storage.nextTeamId());
    }
    storage.getTeams().put(team.getId(), team);
    return team;
  }

  @Override
  public Optional<Team> findById(Long id) {
    return Optional.ofNullable(storage.getTeams().get(id));
  }

  @Override
  public List<Team> findAll() {
    return new ArrayList<>(storage.getTeams().values());
  }

  @Override
  public Optional<Team> findByIdWithCapoAndMembri(Long id) {
    return findById(id);
  }

  @Override
  public Optional<Team> findByMembroOrCapoId(Long utenteId) {
    if (utenteId == null) return Optional.empty();
    return storage.getTeams().values().stream()
        .filter(t -> (t.getCapo() != null && utenteId.equals(t.getCapo().getId()))
            || (t.getMembri() != null && t.getMembri().stream().anyMatch(m -> utenteId.equals(m.getId()))))
        .findFirst();
  }

  @Override
  public int countTeamsIscritti(Long hackathonId) {
    Set<Long> teamIds = storage.getHackathonIscrizioni().get(hackathonId);
    return teamIds == null ? 0 : teamIds.size();
  }

  @Override
  public List<Team> findTeamsIscritti(Long hackathonId) {
    Set<Long> teamIds = storage.getHackathonIscrizioni().get(hackathonId);
    if (teamIds == null || teamIds.isEmpty()) {
      return new ArrayList<>();
    }
    List<Team> result = new ArrayList<>();
    for (Long id : teamIds) {
      Team t = storage.getTeams().get(id);
      if (t != null) {
        result.add(t);
      }
    }
    return result;
  }

  @Override
  public void iscriviTeam(Long hackathonId, Long teamId) {
    storage.getHackathonIscrizioni()
        .computeIfAbsent(hackathonId, k -> ConcurrentHashMap.newKeySet())
        .add(teamId);
  }

  @Override
  public boolean isTeamIscritto(Long hackathonId, Long teamId) {
    Set<Long> ids = storage.getHackathonIscrizioni().get(hackathonId);
    return ids != null && ids.contains(teamId);
  }
}
