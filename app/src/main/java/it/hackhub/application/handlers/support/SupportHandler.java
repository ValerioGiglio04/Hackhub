package it.hackhub.application.handlers.support;

import it.hackhub.application.dto.supporto.RichiestaSupportoCreateDTO;
import it.hackhub.application.dto.supporto.RichiestaSupportoResponseDTO;
import it.hackhub.application.dto.supporto.SegnalazioneViolazioneCreateDTO;
import it.hackhub.application.repositories.associations.StaffHackatonRepository;
import it.hackhub.application.repositories.support.RichiestaSupportoRepository;
import it.hackhub.application.repositories.support.SegnalazioneViolazioneRepository;
import it.hackhub.core.entities.support.RichiestaSupporto;
import it.hackhub.core.entities.support.SegnalazioneViolazione;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Handler per use case Support: richieste supporto, segnalazioni violazioni.
 * Contiene solo la logica operativa; i controlli di ruolo sono nel controller.
 */
@Service
public class SupportHandler {

  private final RichiestaSupportoRepository richiestaSupportoRepository;
  private final SegnalazioneViolazioneRepository segnalazioneViolazioneRepository;
  private final StaffHackatonRepository staffHackatonRepository;

  public SupportHandler(
    RichiestaSupportoRepository richiestaSupportoRepository,
    SegnalazioneViolazioneRepository segnalazioneViolazioneRepository,
    StaffHackatonRepository staffHackatonRepository
  ) {
    this.richiestaSupportoRepository = richiestaSupportoRepository;
    this.segnalazioneViolazioneRepository = segnalazioneViolazioneRepository;
    this.staffHackatonRepository = staffHackatonRepository;
  }

  /**
   * Salva una richiesta di supporto (entità già compilata). Usato dal controller dopo validazione e verifica capo.
   */
  public RichiestaSupporto creaRichiesta(RichiestaSupporto richiesta) {
    return richiestaSupportoRepository.save(richiesta);
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

  /**
   * Ottiene le richieste di supporto per gli hackathon a cui l'utente è assegnato (staff).
   * Il controller deve aver verificato che l'utente sia MENTORE.
   */
  public List<RichiestaSupporto> ottieniRichiestePerMentore(Long mentoreUtenteId) {
    List<Long> hackathonIds = getHackathonIdsPerUtente(mentoreUtenteId);
    return hackathonIds.stream()
      .flatMap(hId -> richiestaSupportoRepository.findByHackathonId(hId).stream())
      .collect(Collectors.toList());
  }

  private List<Long> getHackathonIdsPerUtente(Long utenteId) {
    return staffHackatonRepository
      .findByUtenteId(utenteId)
      .stream()
      .map(sh -> sh.getHackathon().getId())
      .distinct()
      .collect(Collectors.toList());
  }

  public List<RichiestaSupportoResponseDTO> ottieniRichiesteSupportoPerHackathon(
    Long hackathonId
  ) {
    return richiestaSupportoRepository
      .findByHackathonId(hackathonId)
      .stream()
      .map(this::toResponseDTO)
      .collect(Collectors.toList());
  }

  /**
   * Ottiene le richieste di supporto per un team (per visualizzare proposte call).
   */
  public List<RichiestaSupporto> ottieniRichiestePerTeam(Long teamId) {
    return richiestaSupportoRepository.findByTeamId(teamId);
  }

  /**
   * Ottiene le richieste di supporto per un hackathon.
   * Il controller deve aver verificato che l'utente sia mentore di tale hackathon.
   */
  public List<RichiestaSupporto> ottieniRichiestePerHackathon(Long hackathonId) {
    return richiestaSupportoRepository.findByHackathonId(hackathonId);
  }

  /**
   * Ottiene le richieste di supporto di un team limitate agli hackathon a cui l'utente è assegnato.
   * Il controller deve aver verificato che l'utente sia MENTORE.
   */
  public List<RichiestaSupporto> ottieniRichiestePerTeamPerMentore(
    Long mentoreUtenteId,
    Long teamId
  ) {
    List<RichiestaSupporto> richiesteTeam = richiestaSupportoRepository.findByTeamId(teamId);
    if (richiesteTeam.isEmpty()) {
      return List.of();
    }
    List<Long> hackathonIdsMentore = getHackathonIdsPerUtente(mentoreUtenteId);
    return richiesteTeam
      .stream()
      .filter(r -> hackathonIdsMentore.contains(r.getHackathonId()))
      .collect(Collectors.toList());
  }

  /**
   * Ottiene le proposte call (richieste con link proposta) per gli hackathon a cui l'utente è assegnato.
   * Il controller deve aver verificato che l'utente sia MENTORE.
   */
  public List<RichiestaSupporto> ottieniProposteCallPerMentore(Long mentoreUtenteId) {
    List<RichiestaSupporto> richieste = ottieniRichiestePerMentore(mentoreUtenteId);
    return richieste
      .stream()
      .filter(r ->
        r.getLinkCallProposto() != null && !r.getLinkCallProposto().isBlank()
      )
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
