package it.hackhub.presentation.controllers.support;

import it.hackhub.application.dto.supporto.RichiestaSupportoCreateDTO;
import it.hackhub.application.dto.supporto.RichiestaSupportoResponseDTO;
import it.hackhub.application.dto.supporto.SegnalazioneViolazioneCreateDTO;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.application.handlers.support.SupportHandler;
import it.hackhub.application.mappers.SupportoDtoMapper;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.support.RichiestaSupporto;
import it.hackhub.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller Support: richieste di supporto, segnalazioni violazioni, proposte call.
 * Allineato al diagramma di progetto (Use case: Crea richiesta supporto, Visualizza richieste, Segnala violazioni, Proposte call).
 */
@RestController
@RequestMapping("/api/support")
public class SupportController {

  private final SupportHandler supportHandler;
  private final TeamRepository teamRepository;
  private final SupportoDtoMapper supportoDtoMapper;
  private final UtenteRepository utenteRepository;

  public SupportController(
    SupportHandler supportHandler,
    TeamRepository teamRepository,
    SupportoDtoMapper supportoDtoMapper,
    UtenteRepository utenteRepository
  ) {
    this.supportHandler = supportHandler;
    this.teamRepository = teamRepository;
    this.supportoDtoMapper = supportoDtoMapper;
    this.utenteRepository = utenteRepository;
  }

  /** Crea una richiesta di supporto. Solo il capo del team può creare richieste per il proprio team. */
  @PostMapping("/crea-richiesta")
  public ResponseEntity<RichiestaSupportoResponseDTO> creaRichiestaSupporto(
    @Valid @RequestBody RichiestaSupportoCreateDTO dto
  ) {
    Long utenteId = SecurityUtils.getCurrentUserId(utenteRepository);
    Team team = teamRepository.findById(dto.getTeamId())
      .orElseThrow(() -> new EntityNotFoundException("Team", dto.getTeamId()));
    if (team.getCapo() == null || !team.getCapo().getId().equals(utenteId)) {
      throw new UnauthorizedException("Solo il capo del team può creare richieste di supporto per questo team");
    }
    RichiestaSupporto richiesta = supportoDtoMapper.toEntity(dto);
    RichiestaSupporto saved = supportHandler.creaRichiesta(richiesta);
    return ResponseEntity.status(HttpStatus.CREATED).body(supportoDtoMapper.toResponseDTO(saved));
  }

  /** Visualizza tutte le richieste di supporto (organizzatori/mentori; in versione semplificata restituisce tutte). */
  @GetMapping("/richieste")
  public List<RichiestaSupportoResponseDTO> visualizzaRichiesteSupporto() {
    SecurityUtils.getCurrentUserId(utenteRepository);
    return supportHandler.ottieniRichiesteSupporto();
  }

  /** Segnala una violazione (ruolo MENTORE). */
  @PostMapping("/segnala-violazione")
  public ResponseEntity<Void> segnalaViolazione(@Valid @RequestBody SegnalazioneViolazioneCreateDTO dto) {
    Long mentoreId = SecurityUtils.getCurrentUserId(utenteRepository);
    dto.setMentoreSegnalanteUtenteId(mentoreId);
    supportHandler.segnalaViolazione(dto);
    return ResponseEntity.ok().build();
  }

  /** Richieste di supporto per hackathon (filtrate lato client se necessario). */
  @GetMapping("/richieste/hackathon/{hackathonId}")
  public List<RichiestaSupportoResponseDTO> visualizzaRichiestePerHackathon(@PathVariable Long hackathonId) {
    SecurityUtils.getCurrentUserId(utenteRepository);
    return supportHandler.ottieniRichiesteSupporto().stream()
      .filter(r -> hackathonId.equals(r.getHackathonId()))
      .collect(Collectors.toList());
  }

  /** Proposte di call per il team dell'utente corrente (richieste con link call valorizzato). */
  @GetMapping("/proposte-call")
  public List<RichiestaSupportoResponseDTO> visualizzaProposteCall() {
    Long utenteId = SecurityUtils.getCurrentUserId(utenteRepository);
    var teamOpt = teamRepository.findByMembroOrCapoId(utenteId);
    if (teamOpt.isEmpty()) return List.of();
    List<RichiestaSupporto> richieste = supportHandler.ottieniRichiestePerTeam(teamOpt.get().getId());
    return richieste.stream()
      .filter(r -> r.getLinkCallProposto() != null && !r.getLinkCallProposto().isBlank())
      .map(supportoDtoMapper::toResponseDTO)
      .collect(Collectors.toList());
  }

  /** Richieste di supporto per team. */
  @GetMapping("/richieste/team/{teamId}")
  public List<RichiestaSupportoResponseDTO> visualizzaRichiestePerTeam(@PathVariable Long teamId) {
    SecurityUtils.getCurrentUserId(utenteRepository);
    return supportHandler.ottieniRichiestePerTeam(teamId).stream()
      .map(supportoDtoMapper::toResponseDTO)
      .collect(Collectors.toList());
  }
}
