package it.hackhub.presentation.controllers.core;

import it.hackhub.application.dto.TeamResponseDTO;
import it.hackhub.application.dto.hackathon.HackathonCreateDTO;
import it.hackhub.application.dto.hackathon.HackathonResponseDTO;
import it.hackhub.application.dto.hackathon.HackathonStaffAssignmentDTO;
import it.hackhub.application.dto.hackathon.HackathonUpdateDTO;
import it.hackhub.application.dto.hackathon.HackathonWinnerDTO;
import it.hackhub.application.dto.hackathon.InvitoStaffResponseDTO;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.handlers.InvitiStaffHandler;
import it.hackhub.application.handlers.TeamHandler;
import it.hackhub.application.handlers.core.HackathonHandler;
import it.hackhub.application.mappers.HackathonDtoMapper;
import it.hackhub.application.mappers.InvitoStaffDtoMapper;
import it.hackhub.application.repositories.associations.IscrizioneTeamHackathonRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.Team;
import it.hackhub.infrastructure.security.SecurityUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST Hackathon
 */
@RestController
@RequestMapping("/api/hackathon")
public class HackathonController {

  private final HackathonHandler hackathonHandler;
  private final InvitiStaffHandler invitiStaffHandler;
  private final UtenteRepository utenteRepository;
  private final TeamHandler teamHandler;
  private final IscrizioneTeamHackathonRepository iscrizioneTeamHackathonRepository;

  public HackathonController(
    HackathonHandler hackathonHandler,
    InvitiStaffHandler invitiStaffHandler,
    UtenteRepository utenteRepository,
    TeamHandler teamHandler,
    IscrizioneTeamHackathonRepository iscrizioneTeamHackathonRepository
  ) {
    this.hackathonHandler = hackathonHandler;
    this.invitiStaffHandler = invitiStaffHandler;
    this.utenteRepository = utenteRepository;
    this.teamHandler = teamHandler;
    this.iscrizioneTeamHackathonRepository = iscrizioneTeamHackathonRepository;
  }

  @GetMapping("/pubblico")
  public List<HackathonResponseDTO> ottieniElencoPubblico() {
    return hackathonHandler.ottieniTuttiGliHackathon().stream()
        .map(HackathonDtoMapper::toResponseDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/{hackathonId}")
  public HackathonResponseDTO ottieniHackathonPerId(@PathVariable Long hackathonId) {
    return hackathonHandler.ottieniHackathonPerId(hackathonId)
        .map(HackathonDtoMapper::toResponseDTO)
        .orElseThrow(() -> new EntityNotFoundException("Hackathon", hackathonId));
  }

  @PostMapping
  public HackathonResponseDTO creaHackathon(@RequestBody HackathonCreateDTO dto) {
    return hackathonHandler.creaHackathon(dto);
  }

  @PutMapping("/{hackathonId}")
  public HackathonResponseDTO aggiornaHackathon(
    @PathVariable Long hackathonId,
    @RequestBody HackathonUpdateDTO dto
  ) {
    Hackathon h = hackathonHandler.aggiornaHackathon(hackathonId, dto);
    HackathonResponseDTO res = new HackathonResponseDTO();
    res.setId(h.getId());
    res.setNome(h.getNome());
    res.setStato(h.getStato());
    return res;
  }

  @PostMapping("/vincitore")
  public void impostaVincitore(@RequestBody HackathonWinnerDTO dto) {
    hackathonHandler.impostaVincitore(dto.getHackathonId(), dto.getTeamId());
  }

  /** Avvio fase iscrizione (chiamato da HackathonScheduler). */
  public int avviaFaseIscrizione() {
    return hackathonHandler.avviaFaseIscrizione();
  }

  /** Conclusione fase iscrizione (chiamato da HackathonScheduler). */
  public int concludiFaseIscrizione() {
    return hackathonHandler.concludiFaseIscrizione();
  }

  /** Avvio fase svolgimento (chiamato da HackathonScheduler). */
  public int avviaFaseSvolgimento() {
    return hackathonHandler.avviaFaseSvolgimento();
  }

  /** Conclusione fase svolgimento (chiamato da HackathonScheduler). */
  public int concludiFaseSvolgimento() {
    return hackathonHandler.concludiFaseSvolgimento();
  }

  @PostMapping("/invita-staff")
  public InvitoStaffResponseDTO invitaStaff(@RequestBody HackathonStaffAssignmentDTO dto) {
    if (invitiStaffHandler == null) {
      throw new IllegalStateException("InvitiStaffHandler non configurato");
    }
    Long utenteCorrenteId = SecurityUtils.getCurrentUserId(utenteRepository);
    var invito = invitiStaffHandler.invitaStaff(
        dto.getHackathonId(),
        dto.getUtenteId(),
        utenteCorrenteId
    );
    return InvitoStaffDtoMapper.toResponseDTO(invito);
  }

  /** Restituisce il team vincitore dell'hackathon (se proclamato). */
  @GetMapping("/{hackathonId}/vincitore")
  public TeamResponseDTO ottieniVincitoreHackathon(@PathVariable Long hackathonId) {
    Hackathon h = hackathonHandler.ottieniHackathonPerId(hackathonId)
        .orElseThrow(() -> new EntityNotFoundException("Hackathon", hackathonId));
    if (h.getTeamVincitore() == null) {
      throw new EntityNotFoundException("Nessun vincitore assegnato per questo hackathon");
    }
    return teamHandler.toResponseDTO(h.getTeamVincitore());
  }

  /** Restituisce l'elenco dei team iscritti all'hackathon. */
  @GetMapping("/{hackathonId}/team-iscritti")
  public List<TeamResponseDTO> ottieniTeamIscrittiHackathon(@PathVariable Long hackathonId) {
    hackathonHandler.ottieniHackathonPerId(hackathonId)
        .orElseThrow(() -> new EntityNotFoundException("Hackathon", hackathonId));
    return iscrizioneTeamHackathonRepository.findByHackathonId(hackathonId).stream()
        .map(isc -> isc.getTeam())
        .filter(t -> t != null)
        .map(teamHandler::toResponseDTO)
        .collect(Collectors.toList());
  }
}
