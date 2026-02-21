package it.hackhub.application.mappers;

import it.hackhub.application.dto.supporto.RichiestaSupportoCreateDTO;
import it.hackhub.application.dto.supporto.RichiestaSupportoResponseDTO;
import it.hackhub.core.entities.support.RichiestaSupporto;
import java.time.LocalDateTime;

/**
 * Mapper per RichiestaSupporto / DTO.
 */
public class SupportoDtoMapper {

  public RichiestaSupporto toEntity(RichiestaSupportoCreateDTO dto) {
    RichiestaSupporto r = new RichiestaSupporto();
    r.setTeamId(dto.getTeamId());
    r.setHackathonId(dto.getHackathonId());
    r.setDescrizione(dto.getDescrizione());
    r.setStato(dto.getStato() != null ? dto.getStato() : "APERTA");
    r.setDataRichiesta(LocalDateTime.now());
    return r;
  }

  public RichiestaSupportoResponseDTO toResponseDTO(RichiestaSupporto r) {
    RichiestaSupportoResponseDTO dto = new RichiestaSupportoResponseDTO();
    dto.setId(r.getId());
    dto.setTeamId(r.getTeamId());
    dto.setHackathonId(r.getHackathonId());
    dto.setDescrizione(r.getDescrizione());
    dto.setDataRichiesta(r.getDataRichiesta());
    dto.setStato(r.getStato());
    dto.setLinkCallProposto(r.getLinkCallProposto());
    return dto;
  }
}
