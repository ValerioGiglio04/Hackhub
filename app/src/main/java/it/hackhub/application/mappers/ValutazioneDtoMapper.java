package it.hackhub.application.mappers;

import it.hackhub.application.dto.hackathon.HackathonSummaryDTO;
import it.hackhub.application.dto.team.TeamSummaryDTO;
import it.hackhub.application.dto.utente.UtenteSummaryDTO;
import it.hackhub.application.dto.valutazioni.SottomissioneSummaryDTO;
import it.hackhub.application.dto.valutazioni.ValutazioneCreateDTO;
import it.hackhub.application.dto.valutazioni.ValutazioneResponseDTO;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.SottomissioneRepository;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Sottomissione;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.core.entities.core.Valutazione;
import org.springframework.stereotype.Component;

@Component
public class ValutazioneDtoMapper {

  private final SottomissioneRepository sottomissioneRepository;
  private final UtenteRepository utenteRepository;
  private final TeamRepository teamRepository;
  private final HackathonRepository hackathonRepository;

  public ValutazioneDtoMapper(
    SottomissioneRepository sottomissioneRepository,
    UtenteRepository utenteRepository,
    TeamRepository teamRepository,
    HackathonRepository hackathonRepository
  ) {
    this.sottomissioneRepository = sottomissioneRepository;
    this.utenteRepository = utenteRepository;
    this.teamRepository = teamRepository;
    this.hackathonRepository = hackathonRepository;
  }

  public Valutazione toEntity(ValutazioneCreateDTO dto) {
    Sottomissione sottomissione = sottomissioneRepository
      .findById(dto.getSottomissioneId())
      .orElseThrow(() -> new EntityNotFoundException("Sottomissione", dto.getSottomissioneId()));
    Utente giudice = utenteRepository
      .findById(dto.getGiudiceId())
      .orElseThrow(() -> new EntityNotFoundException("Utente", dto.getGiudiceId()));
    Valutazione v = new Valutazione();
    v.setSottomissioneId(sottomissione.getId());
    v.setGiudiceId(giudice.getId());
    v.setPunteggio(dto.getPunteggio());
    v.setCommento(dto.getCommento());
    return v;
  }

  public ValutazioneResponseDTO toResponseDTO(Valutazione valutazione) {
    ValutazioneResponseDTO dto = new ValutazioneResponseDTO();
    dto.setId(valutazione.getId());
    dto.setPunteggio(valutazione.getPunteggio());
    dto.setCommento(valutazione.getCommento());
    dto.setDataValutazione(valutazione.getDataValutazione());
    sottomissioneRepository.findById(valutazione.getSottomissioneId()).ifPresent(s -> {
      SottomissioneSummaryDTO subDto = new SottomissioneSummaryDTO();
      subDto.setId(s.getId());
      teamRepository.findById(s.getTeamId()).ifPresent(t -> {
        TeamSummaryDTO td = new TeamSummaryDTO();
        td.setId(t.getId());
        td.setNome(t.getNome());
        subDto.setTeam(td);
      });
      hackathonRepository.findById(s.getHackathonId()).ifPresent(h -> {
        HackathonSummaryDTO hd = new HackathonSummaryDTO();
        hd.setId(h.getId());
        hd.setNome(h.getNome());
        subDto.setHackathon(hd);
      });
      dto.setSottomissione(subDto);
    });
    utenteRepository.findById(valutazione.getGiudiceId()).ifPresent(g -> {
      UtenteSummaryDTO gDto = new UtenteSummaryDTO();
      gDto.setId(g.getId());
      gDto.setNome(g.getNome());
      gDto.setCognome(g.getCognome());
      dto.setGiudice(gDto);
    });
    return dto;
  }
}
