package it.hackhub.application.mappers;

import it.hackhub.application.dto.hackathon.HackathonResponseDTO;
import it.hackhub.core.entities.core.Hackathon;

/**
 * Mapper Hackathon ↔ HackathonResponseDTO (use case Visualizza informazioni hackathon).
 */
public final class HackathonDtoMapper {

  private HackathonDtoMapper() {}

  public static HackathonResponseDTO toResponseDTO(Hackathon hackathon) {
    if (hackathon == null) return null;
    HackathonResponseDTO dto = new HackathonResponseDTO();
    dto.setId(hackathon.getId());
    dto.setNome(hackathon.getNome());
    dto.setStato(hackathon.getStato());
    return dto;
  }
}
