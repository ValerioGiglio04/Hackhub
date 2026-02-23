package it.hackhub.application.mappers;

import it.hackhub.application.dto.TeamCreateDTO;
import it.hackhub.application.dto.team.TeamResponseDTO;
import it.hackhub.application.dto.team.TeamSummaryDTO;
import it.hackhub.application.dto.utente.UtenteSummaryDTO;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TeamDtoMapper {

  private final UtenteRepository utenteRepository;
  private final String defaultPaypalEmail;
  private final String paypalMode;

  public TeamDtoMapper(
    UtenteRepository utenteRepository,
    @Value("${paypal.default.email:}") String defaultPaypalEmail,
    @Value("${paypal.mode:sandbox}") String paypalMode
  ) {
    this.utenteRepository = utenteRepository;
    this.defaultPaypalEmail = defaultPaypalEmail != null ? defaultPaypalEmail : "";
    this.paypalMode = paypalMode != null ? paypalMode : "sandbox";
  }

  public Team toEntity(TeamCreateDTO dto) {
    Utente capo = utenteRepository
      .findById(dto.getCapoId())
      .orElseThrow(() -> new EntityNotFoundException("Utente", dto.getCapoId()));
    Team team = new Team();
    team.setNome(dto.getNome());
    team.setCapo(capo);
    team.setMembri(new java.util.ArrayList<>());
    if ("sandbox".equalsIgnoreCase(paypalMode) && defaultPaypalEmail != null && !defaultPaypalEmail.isEmpty()) {
      team.setEmailPaypal(defaultPaypalEmail);
    }
    return team;
  }

  public TeamResponseDTO toResponseDTO(Team team) {
    TeamResponseDTO dto = new TeamResponseDTO();
    dto.setId(team.getId());
    dto.setNome(team.getNome());
    if (team.getCapo() != null) {
      Utente c = team.getCapo();
      UtenteSummaryDTO capoDto = new UtenteSummaryDTO();
      capoDto.setId(c.getId());
      capoDto.setNome(c.getNome());
      capoDto.setCognome(c.getCognome());
      dto.setCapo(capoDto);
    }
    if (team.getMembri() != null) {
      List<UtenteSummaryDTO> membri = team.getMembri().stream().map(u -> {
        UtenteSummaryDTO ud = new UtenteSummaryDTO();
        ud.setId(u.getId());
        ud.setNome(u.getNome());
        ud.setCognome(u.getCognome());
        return ud;
      }).collect(Collectors.toList());
      dto.setMembri(membri);
      dto.setNumeroMembri(membri.size() + (team.getCapo() != null ? 1 : 0));
    } else {
      dto.setMembri(Collections.emptyList());
      dto.setNumeroMembri(team.getCapo() != null ? 1 : 0);
    }
    return dto;
  }

  public TeamSummaryDTO toSummaryDTO(Team team) {
    TeamSummaryDTO dto = new TeamSummaryDTO();
    dto.setId(team.getId());
    dto.setNome(team.getNome());
    return dto;
  }
}
