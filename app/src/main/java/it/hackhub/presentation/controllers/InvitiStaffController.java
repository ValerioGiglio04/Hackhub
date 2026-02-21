package it.hackhub.presentation.controllers;

import it.hackhub.application.dto.hackathon.GestisciInvitoStaffDTO;
import it.hackhub.application.dto.hackathon.InvitoStaffResponseDTO;
import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.handlers.InvitiStaffHandler;
import it.hackhub.application.mappers.InvitoStaffDtoMapper;
import it.hackhub.core.entities.associations.InvitoStaff;

/**
 * Controller per gestione inviti staff (accetta/rifiuta).
 */
public class InvitiStaffController {

  private final InvitiStaffHandler invitiStaffHandler;

  public InvitiStaffController(InvitiStaffHandler invitiStaffHandler) {
    this.invitiStaffHandler = invitiStaffHandler;
  }

  /**
   * Gestisce un invito staff: ACCETTA o RIFIUTA.
   * ACCETTA → 200 senza body; RIFIUTA → 200 + InvitoStaffResponseDTO.
   */
  public Object gestisciInvito(Long invitoId, GestisciInvitoStaffDTO dto, Long utenteCorrenteId) {
    if (dto.getAzione() == null || dto.getAzione().isBlank()) {
      throw new BusinessLogicException("Azione non valida");
    }
    String azione = dto.getAzione().toUpperCase();
    switch (azione) {
      case "ACCETTA":
        invitiStaffHandler.accettaInvitoStaff(invitoId, utenteCorrenteId);
        return null;
      case "RIFIUTA":
        InvitoStaff invito = invitiStaffHandler.rifiutaInvitoStaff(invitoId, utenteCorrenteId);
        return InvitoStaffDtoMapper.toResponseDTO(invito);
      default:
        throw new BusinessLogicException("Azione non valida: " + dto.getAzione());
    }
  }

}
