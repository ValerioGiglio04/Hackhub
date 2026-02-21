package it.hackhub.application.handlers;

import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.handlers.core.HackathonHandler;
import it.hackhub.application.repositories.associations.InvitoStaffRepository;
import it.hackhub.core.entities.associations.InvitoStaff;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Handler per inviti staff hackathon (accetta/rifiuta).
 */
public class InvitiStaffHandler {

  private final InvitoStaffRepository invitoStaffRepository;
  private final HackathonHandler hackathonHandler;

  public InvitiStaffHandler(
    InvitoStaffRepository invitoStaffRepository,
    HackathonHandler hackathonHandler
  ) {
    this.invitoStaffRepository = invitoStaffRepository;
    this.hackathonHandler = hackathonHandler;
  }

  public void accettaInvitoStaff(Long invitoId, Long utenteCorrenteId) {
    InvitoStaff invito = invitoStaffRepository.findByIdWithDetails(invitoId)
        .orElseThrow(() -> new EntityNotFoundException("Invito staff", invitoId));
    if (invito.getStato() != InvitoStaff.StatoInvito.PENDING) {
      throw new BusinessLogicException("Questo invito non è più valido");
    }
    if (!invito.getUtenteInvitato().getId().equals(utenteCorrenteId)) {
      throw new BusinessLogicException("Puoi accettare solo gli inviti rivolti a te");
    }
    hackathonHandler.assegnaStaff(invito.getHackathon().getId(), invito.getUtenteInvitato().getId());
    invito.setStato(InvitoStaff.StatoInvito.ACCETTATO);
    invitoStaffRepository.save(invito);
  }

  public InvitoStaff rifiutaInvitoStaff(Long invitoId, Long utenteCorrenteId) {
    InvitoStaff invito = invitoStaffRepository.findByIdWithDetails(invitoId)
        .orElseThrow(() -> new EntityNotFoundException("Invito staff", invitoId));
    if (invito.getStato() != InvitoStaff.StatoInvito.PENDING) {
      throw new BusinessLogicException("Questo invito non è più valido");
    }
    if (!invito.getUtenteInvitato().getId().equals(utenteCorrenteId)) {
      throw new BusinessLogicException("Puoi rifiutare solo gli inviti rivolti a te");
    }
    invito.setStato(InvitoStaff.StatoInvito.RIFIUTATO);
    return invitoStaffRepository.save(invito);
  }

  public List<InvitoStaff> ottieniInvitiRicevutiPending(Long utenteId) {
    return invitoStaffRepository.findByUtenteInvitatoIdAndStato(utenteId, InvitoStaff.StatoInvito.PENDING);
  }
}
