package it.hackhub.application.mappers;

import it.hackhub.application.dto.SottomissioneCreateDTO;
import it.hackhub.application.dto.SottomissioneUpdateDTO;
import it.hackhub.application.dto.hackathon.HackathonSummaryDTO;
import it.hackhub.application.dto.team.TeamSummaryDTO;
import it.hackhub.application.dto.valutazioni.SottomissioneResponseDTO;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.core.entities.core.Sottomissione;
import it.hackhub.core.entities.core.Valutazione;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SottomissioneDtoMapper {

  private final TeamRepository teamRepository;
  private final HackathonRepository hackathonRepository;

  public SottomissioneDtoMapper(
    TeamRepository teamRepository,
    HackathonRepository hackathonRepository
  ) {
    this.teamRepository = teamRepository;
    this.hackathonRepository = hackathonRepository;
  }

  public Sottomissione toEntity(SottomissioneCreateDTO dto) {
    it.hackhub.core.entities.core.Team team = teamRepository
      .findById(dto.getTeamId())
      .orElseThrow(() -> new EntityNotFoundException("Team", dto.getTeamId()));
    it.hackhub.core.entities.core.Hackathon hackathon = hackathonRepository
      .findById(dto.getHackathonId())
      .orElseThrow(() -> new EntityNotFoundException("Hackathon", dto.getHackathonId()));
    Sottomissione s = new Sottomissione();
    s.setTeamId(team.getId());
    s.setHackathonId(hackathon.getId());
    s.setLinkProgetto(dto.getLinkProgetto());
    return s;
  }

  public void updateEntity(Sottomissione sottomissione, SottomissioneUpdateDTO dto) {
    sottomissione.setLinkProgetto(dto.getLinkProgetto());
  }

  public SottomissioneResponseDTO toResponseDTO(
    Sottomissione sottomissione,
    List<Valutazione> valutazioni
  ) {
    SottomissioneResponseDTO dto = new SottomissioneResponseDTO();
    dto.setId(sottomissione.getId());
    teamRepository.findById(sottomissione.getTeamId()).ifPresent(t -> {
      TeamSummaryDTO td = new TeamSummaryDTO();
      td.setId(t.getId());
      td.setNome(t.getNome());
      dto.setTeam(td);
    });
    hackathonRepository.findById(sottomissione.getHackathonId()).ifPresent(h -> {
      HackathonSummaryDTO hd = new HackathonSummaryDTO();
      hd.setId(h.getId());
      hd.setNome(h.getNome());
      dto.setHackathon(hd);
    });
    dto.setLinkProgetto(sottomissione.getLinkProgetto());
    dto.setDataCaricamento(sottomissione.getDataCaricamento());
    dto.setDataUltimoUpdate(sottomissione.getDataUltimoUpdate());
    if (valutazioni != null && !valutazioni.isEmpty()) {
      dto.setNumeroValutazioni(valutazioni.size());
      dto.setPunteggioMedio(
        valutazioni.stream().mapToInt(Valutazione::getPunteggio).average().orElse(0.0)
      );
    }
    return dto;
  }
}
