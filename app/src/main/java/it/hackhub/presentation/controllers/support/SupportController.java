package it.hackhub.presentation.controllers.support;

import it.hackhub.application.dto.supporto.RichiestaSupportoCreateDTO;
import it.hackhub.application.dto.supporto.RichiestaSupportoResponseDTO;
import it.hackhub.application.dto.supporto.SegnalazioneViolazioneCreateDTO;
import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.handlers.support.SupportHandler;
import it.hackhub.application.mappers.SupportoDtoMapper;
import it.hackhub.application.repositories.associations.StaffHackatonRepository;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.core.entities.support.RichiestaSupporto;
import it.hackhub.infrastructure.security.AuthorizationUtils;
import it.hackhub.infrastructure.security.annotations.RequiresRole;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller Support: richieste di supporto, segnalazioni violazioni, proposte call.
 */
@RestController
@RequestMapping("/api/support")
public class SupportController {

  private final SupportHandler supportHandler;
  private final TeamRepository teamRepository;
  private final SupportoDtoMapper supportoDtoMapper;
  private final UtenteRepository utenteRepository;
  private final StaffHackatonRepository staffHackatonRepository;

  public SupportController(
    SupportHandler supportHandler,
    TeamRepository teamRepository,
    SupportoDtoMapper supportoDtoMapper,
    UtenteRepository utenteRepository,
    StaffHackatonRepository staffHackatonRepository
  ) {
    this.supportHandler = supportHandler;
    this.teamRepository = teamRepository;
    this.supportoDtoMapper = supportoDtoMapper;
    this.utenteRepository = utenteRepository;
    this.staffHackatonRepository = staffHackatonRepository;
  }

  /** @requiresRole Richiede che l'utente sia capo del team */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO, requiresTeamLeader = true)
  @PostMapping("/crea-richiesta")
  public ResponseEntity<RichiestaSupportoResponseDTO> creaRichiestaSupporto(
    @Valid @RequestBody RichiestaSupportoCreateDTO dto
  ) {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    //E' necessario verificare che l'utente sia capo del team per creare una richiesta di supporto
    AuthorizationUtils.requireTeamLeader(
      utente,
      dto.getTeamId(),
      teamRepository
    );
    RichiestaSupporto richiesta = supportoDtoMapper.toEntity(dto);
    RichiestaSupporto saved = supportHandler.creaRichiesta(richiesta);
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(supportoDtoMapper.toResponseDTO(saved));
  }

  /** Solo il mentore vede le richieste di supporto (per gli hackathon di cui è mentore). */
  @RequiresRole(role = Utente.RuoloStaff.MENTORE)
  @GetMapping("/richieste")
  public List<RichiestaSupportoResponseDTO> visualizzaRichiesteSupporto() {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    List<RichiestaSupporto> richieste = supportHandler.ottieniRichiestePerMentore(
      utente.getId()
    );
    return richieste
      .stream()
      .map(supportoDtoMapper::toResponseDTO)
      .collect(Collectors.toList());
  }

  @RequiresRole(
    role = Utente.RuoloStaff.MENTORE,
    requiresHackathonAssignment = true
  )
  @PostMapping("/segnala-violazione")
  public ResponseEntity<Void> segnalaViolazione(
    @Valid @RequestBody SegnalazioneViolazioneCreateDTO dto
  ) {
    Utente mentore = AuthorizationUtils.getCurrentUser(utenteRepository);
    dto.setMentoreSegnalanteUtenteId(mentore.getId());
    supportHandler.segnalaViolazione(dto);
    return ResponseEntity.ok().build();
  }

  /** Solo il mentore dell'hackathon vede le richieste di supporto per quell'hackathon. */
  @RequiresRole(role = Utente.RuoloStaff.MENTORE)
  @GetMapping("/richieste/hackathon/{hackathonId}")
  public List<RichiestaSupportoResponseDTO> visualizzaRichiestePerHackathon(
    @PathVariable Long hackathonId
  ) {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    boolean isMentoreDiQuestoHackathon = staffHackatonRepository
      .findByUtenteId(utente.getId())
      .stream()
      .anyMatch(sh -> hackathonId.equals(sh.getHackathon().getId()));
    if (!isMentoreDiQuestoHackathon) {
      throw new UnauthorizedException(
        "Puoi visualizzare solo le richieste di supporto degli hackathon di cui sei mentore."
      );
    }
    List<RichiestaSupporto> richieste = supportHandler.ottieniRichiestePerHackathon(
      hackathonId
    );
    return richieste
      .stream()
      .map(supportoDtoMapper::toResponseDTO)
      .collect(Collectors.toList());
  }

  /** Solo il mentore vede le proposte call (per gli hackathon di cui è mentore). */
  @RequiresRole(role = Utente.RuoloStaff.MENTORE)
  @GetMapping("/proposte-call")
  public List<RichiestaSupportoResponseDTO> visualizzaProposteCall() {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    List<RichiestaSupporto> richieste = supportHandler.ottieniProposteCallPerMentore(
      utente.getId()
    );
    return richieste
      .stream()
      .map(supportoDtoMapper::toResponseDTO)
      .collect(Collectors.toList());
  }

  /** Solo il mentore vede le richieste di un team (solo per gli hackathon di cui è mentore). */
  @RequiresRole(role = Utente.RuoloStaff.MENTORE)
  @GetMapping("/richieste/team/{teamId}")
  public List<RichiestaSupportoResponseDTO> visualizzaRichiestePerTeam(
    @PathVariable Long teamId
  ) {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    List<RichiestaSupporto> richieste = supportHandler.ottieniRichiestePerTeamPerMentore(
      utente.getId(),
      teamId
    );
    return richieste
      .stream()
      .map(supportoDtoMapper::toResponseDTO)
      .collect(Collectors.toList());
  }
}
