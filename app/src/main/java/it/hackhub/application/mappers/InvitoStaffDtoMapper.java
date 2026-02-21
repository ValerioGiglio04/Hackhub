package it.hackhub.application.mappers;

import it.hackhub.application.dto.hackathon.InvitoStaffResponseDTO;
import it.hackhub.core.entities.associations.InvitoStaff;

/**
 * Mappa InvitoStaff in InvitoStaffResponseDTO.
 */
public final class InvitoStaffDtoMapper {

  private InvitoStaffDtoMapper() {}

  public static InvitoStaffResponseDTO toResponseDTO(InvitoStaff invito) {
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
