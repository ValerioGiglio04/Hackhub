package it.hackhub.infrastructure.persistence.impl;

import it.hackhub.application.repositories.associations.InvitoStaffRepository;
import it.hackhub.core.entities.associations.InvitoStaff;
import it.hackhub.infrastructure.persistence.StorageInMemoria;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InvitoStaffRepositoryImpl implements InvitoStaffRepository {

  private final StorageInMemoria storage;

  public InvitoStaffRepositoryImpl(StorageInMemoria storage) {
    this.storage = storage;
  }

  @Override
  public InvitoStaff save(InvitoStaff invito) {
    if (invito.getId() == null) {
      invito.setId(storage.nextInvitoStaffId());
    }
    storage.getInvitiStaff().put(invito.getId(), invito);
    return invito;
  }

  @Override
  public Optional<InvitoStaff> findById(Long id) {
    return Optional.ofNullable(storage.getInvitiStaff().get(id));
  }

  @Override
  public Optional<InvitoStaff> findByIdWithDetails(Long id) {
    return findById(id);
  }

  @Override
  public List<InvitoStaff> findByUtenteInvitatoIdAndStato(Long utenteInvitatoId, InvitoStaff.StatoInvito stato) {
    return storage.getInvitiStaff().values().stream()
        .filter(i -> utenteInvitatoId != null && i.getUtenteInvitato() != null && utenteInvitatoId.equals(i.getUtenteInvitato().getId()))
        .filter(i -> stato == i.getStato())
        .collect(Collectors.toList());
  }

  @Override
  public List<InvitoStaff> findByHackathonIdAndStato(Long hackathonId, InvitoStaff.StatoInvito stato) {
    return storage.getInvitiStaff().values().stream()
        .filter(i -> hackathonId != null && i.getHackathon() != null && hackathonId.equals(i.getHackathon().getId()))
        .filter(i -> stato == i.getStato())
        .collect(Collectors.toList());
  }
}
