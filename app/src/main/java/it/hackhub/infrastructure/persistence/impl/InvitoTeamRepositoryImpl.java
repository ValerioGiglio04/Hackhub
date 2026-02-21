package it.hackhub.infrastructure.persistence.impl;

import it.hackhub.application.repositories.associations.InvitoTeamRepository;
import it.hackhub.core.entities.associations.InvitoTeam;
import it.hackhub.infrastructure.persistence.StorageInMemoria;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InvitoTeamRepositoryImpl implements InvitoTeamRepository {

  private final StorageInMemoria storage;

  public InvitoTeamRepositoryImpl(StorageInMemoria storage) {
    this.storage = storage;
  }

  @Override
  public InvitoTeam save(InvitoTeam invito) {
    if (invito.getId() == null) {
      invito.setId(storage.nextInvitoTeamId());
    }
    storage.getInvitiTeam().put(invito.getId(), invito);
    return invito;
  }

  @Override
  public Optional<InvitoTeam> findById(Long id) {
    return Optional.ofNullable(storage.getInvitiTeam().get(id));
  }

  @Override
  public Optional<InvitoTeam> findByIdWithDetails(Long id) {
    return findById(id);
  }

  @Override
  public List<InvitoTeam> findByUtenteInvitatoIdAndStato(Long utenteInvitatoId, InvitoTeam.StatoInvito stato) {
    return storage.getInvitiTeam().values().stream()
        .filter(i -> utenteInvitatoId != null && utenteInvitatoId.equals(i.getUtenteInvitato() != null ? i.getUtenteInvitato().getId() : null))
        .filter(i -> stato == i.getStato())
        .collect(Collectors.toList());
  }

  @Override
  public List<InvitoTeam> findByTeamIdAndStato(Long teamId, InvitoTeam.StatoInvito stato) {
    return storage.getInvitiTeam().values().stream()
        .filter(i -> teamId != null && i.getTeam() != null && teamId.equals(i.getTeam().getId()))
        .filter(i -> stato == i.getStato())
        .collect(Collectors.toList());
  }

  @Override
  public Optional<InvitoTeam> findByTeamIdAndUtenteInvitatoIdAndStato(
    Long teamId,
    Long utenteInvitatoId,
    InvitoTeam.StatoInvito stato
  ) {
    return storage.getInvitiTeam().values().stream()
        .filter(i -> teamId != null && i.getTeam() != null && teamId.equals(i.getTeam().getId()))
        .filter(i -> utenteInvitatoId != null && i.getUtenteInvitato() != null && utenteInvitatoId.equals(i.getUtenteInvitato().getId()))
        .filter(i -> stato == i.getStato())
        .findFirst();
  }
}
