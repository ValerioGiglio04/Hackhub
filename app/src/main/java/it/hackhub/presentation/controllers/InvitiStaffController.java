package it.hackhub.presentation.controllers;

import it.hackhub.application.dto.hackathon.GestisciInvitoStaffDTO;
import it.hackhub.application.dto.hackathon.InvitoStaffResponseDTO;
import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.handlers.InvitiStaffHandler;
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
        return toInvitoStaffResponseDTO(invito);
      default:
        throw new BusinessLogicException("Azione non valida: " + dto.getAzione());
    }
  }

  private static InvitoStaffResponseDTO toInvitoStaffResponseDTO(InvitoStaff invito) {
    InvitoStaffResponseDTO out = new InvitoStaffResponseDTO();
    out.setId(invito.getId());
    out.setStato(invito.getStato());
    out.setDataInvito(invito.getDataInvito());
    if (invito.getHackathon() != null) {
      out.setHackathonId(invito.getHackathon().getId());
      out.setNomeHackathon(invito.getHackathon().getNome());
    }
    if (invito.getUtenteInvitato() != null) {
      out.setUtenteInvitatoId(invito.getUtenteInvitato().getId());
      String n = invito.getUtenteInvitato().getNome();
      String c = invito.getUtenteInvitato().getCognome();
      out.setUtenteInvitatoNome(n != null && c != null ? n + " " + c : (n != null ? n : c));
    }
    if (invito.getMittente() != null) {
      out.setMittenteId(invito.getMittente().getId());
    }
    return out;
  }
}
