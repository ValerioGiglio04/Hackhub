package it.hackhub.application.handlers.support;

import it.hackhub.application.dto.supporto.RichiestaSupportoCreateDTO;
import it.hackhub.application.dto.supporto.RichiestaSupportoResponseDTO;
import it.hackhub.application.dto.supporto.SegnalazioneViolazioneCreateDTO;
import it.hackhub.application.repositories.support.RichiestaSupportoRepository;
import it.hackhub.application.repositories.support.SegnalazioneViolazioneRepository;
import it.hackhub.core.entities.support.RichiestaSupporto;
import it.hackhub.core.entities.support.SegnalazioneViolazione;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler per use case Support: richieste supporto, segnalazioni violazioni.
 */
public class SupportHandler {

  private final RichiestaSupportoRepository richiestaSupportoRepository;
  private final SegnalazioneViolazioneRepository segnalazioneViolazioneRepository;

  public SupportHandler(
    RichiestaSupportoRepository richiestaSupportoRepository,
    SegnalazioneViolazioneRepository segnalazioneViolazioneRepository
  ) {
    this.richiestaSupportoRepository = richiestaSupportoRepository;
    this.segnalazioneViolazioneRepository = segnalazioneViolazioneRepository;
  }

  public RichiestaSupportoResponseDTO creaRichiestaSupporto(
    RichiestaSupportoCreateDTO dto
  ) {
    RichiestaSupporto r = new RichiestaSupporto();
    r.setTeamId(dto.getTeamId());
    r.setHackathonId(dto.getHackathonId());
    r.setDescrizione(dto.getDescrizione());
    r.setStato(dto.getStato() != null ? dto.getStato() : "APERTA");
    r.setDataRichiesta(LocalDateTime.now());
    RichiestaSupporto saved = richiestaSupportoRepository.save(r);
    return toResponseDTO(saved);
  }

  public List<RichiestaSupportoResponseDTO> ottieniRichiesteSupporto() {
    return richiestaSupportoRepository
      .findAll()
      .stream()
      .map(this::toResponseDTO)
      .collect(Collectors.toList());
  }

  public void segnalaViolazione(SegnalazioneViolazioneCreateDTO dto) {
    SegnalazioneViolazione s = new SegnalazioneViolazione();
    s.setTeamSegnalatoId(dto.getTeamSegnalatoId());
    s.setMentoreSegnalanteId(dto.getMentoreSegnalanteUtenteId());
    s.setHackathonId(dto.getHackathonId());
    s.setDescrizione(dto.getDescrizione());
    s.setDataSegnalazione(LocalDateTime.now());
    segnalazioneViolazioneRepository.save(s);
  }

  private RichiestaSupportoResponseDTO toResponseDTO(RichiestaSupporto r) {
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
