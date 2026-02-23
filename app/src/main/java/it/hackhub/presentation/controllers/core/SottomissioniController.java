package it.hackhub.presentation.controllers.core;

import it.hackhub.application.dto.SottomissioneCreateDTO;
import it.hackhub.application.dto.SottomissioneUpdateDTO;
import it.hackhub.application.dto.common.StandardResponse;
import it.hackhub.application.dto.valutazioni.SottomissioneResponseDTO;
import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.exceptions.submission.SubmissionDeadlinePassedException;
import it.hackhub.application.exceptions.validation.InvalidGitHubLinkException;
import it.hackhub.application.handlers.core.SottomissioneHandler;
import it.hackhub.application.handlers.core.ValutazioneHandler;
import it.hackhub.application.mappers.SottomissioneDtoMapper;
import it.hackhub.application.repositories.associations.IscrizioneTeamHackathonRepository;
import it.hackhub.application.repositories.associations.StaffHackatonRepository;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.Sottomissione;
import it.hackhub.core.entities.core.StatoHackathon;
import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.core.entities.core.Valutazione;
import it.hackhub.infrastructure.security.AuthorizationUtils;
import it.hackhub.infrastructure.security.annotations.RequiresRole;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;

/**
 * Controller Sottomissioni: logica allineata al progetto di riferimento.
 * Visualizza sottomissioni (team/staff/giudice), Invia/Aggiorna sottomissione (membro team).
 */
@RestController
@RequestMapping("/api/sottomissioni")
public class SottomissioniController {

  private final SottomissioneHandler sottomissioneHandler;
  private final ValutazioneHandler valutazioneHandler;
  private final SottomissioneDtoMapper sottomissioneDtoMapper;
  private final TeamRepository teamRepository;
  private final UtenteRepository utenteRepository;
  private final HackathonRepository hackathonRepository;
  private final StaffHackatonRepository staffHackatonRepository;
  private final IscrizioneTeamHackathonRepository iscrizioneTeamHackathonRepository;

  public SottomissioniController(
    SottomissioneHandler sottomissioneHandler,
    ValutazioneHandler valutazioneHandler,
    SottomissioneDtoMapper sottomissioneDtoMapper,
    TeamRepository teamRepository,
    UtenteRepository utenteRepository,
    HackathonRepository hackathonRepository,
    StaffHackatonRepository staffHackatonRepository,
    IscrizioneTeamHackathonRepository iscrizioneTeamHackathonRepository
  ) {
    this.sottomissioneHandler = sottomissioneHandler;
    this.valutazioneHandler = valutazioneHandler;
    this.sottomissioneDtoMapper = sottomissioneDtoMapper;
    this.teamRepository = teamRepository;
    this.utenteRepository = utenteRepository;
    this.hackathonRepository = hackathonRepository;
    this.staffHackatonRepository = staffHackatonRepository;
    this.iscrizioneTeamHackathonRepository = iscrizioneTeamHackathonRepository;
  }

  /** Visualizza le sottomissioni del team dell'utente (membro team). */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @GetMapping
  public List<SottomissioneResponseDTO> ottieniTutteLeSottomissioni() {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    List<Sottomissione> sottomissioni = teamRepository
      .findByMembroOrCapoId(utente.getId())
      .map(team ->
        sottomissioneHandler.ottieniSottomissioniPerTeam(team.getId())
      )
      .orElse(List.of());
    return sottomissioni
      .stream()
      .map(s ->
        sottomissioneDtoMapper.toResponseDTO(
          s,
          valutazioneHandler.ottieniValutazioniPerSottomissione(s.getId())
        )
      )
      .collect(Collectors.toList());
  }

  /** Invia sottomissione: solo membro del team; hackathon IN_CORSO; una sola sottomissione per team per hackathon. */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO, requiresTeamMember = true)
  @PostMapping("/invia")
  public StandardResponse<SottomissioneResponseDTO> inviaSottomissione(
    @Valid @RequestBody SottomissioneCreateDTO dto
  ) throws SubmissionDeadlinePassedException, InvalidGitHubLinkException {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    //E' necessario verificare che l'utente sia membro del team
    //perche' altrimenti l'utente potrebbe inviare sottomissioni per altri team
    AuthorizationUtils.requireTeamMember(
      utente,
      dto.getTeamId(),
      teamRepository
    );

    Team team = teamRepository
      .findById(dto.getTeamId())
      .orElseThrow(() -> new EntityNotFoundException("Team", dto.getTeamId()));

    List<Hackathon> tuttiHackathon = hackathonRepository.findAll();
    List<Hackathon> hackathonAttivi = tuttiHackathon
      .stream()
      .filter(h -> h.getStato() == StatoHackathon.IN_CORSO)
      .filter(h ->
        iscrizioneTeamHackathonRepository
          .findByTeamIdAndHackathonId(dto.getTeamId(), h.getId())
          .isPresent()
      )
      .collect(Collectors.toList());

    if (hackathonAttivi.isEmpty()) {
      throw new BusinessLogicException(
        "Il team '" +
        team.getNome() +
        "' non ha hackathon attivi. È possibile inviare sottomissioni solo durante hackathon attivi."
      );
    }

    Hackathon hackathonAttivo = hackathonAttivi.get(0);
    if (
      hackathonAttivo.getDataInizio() != null &&
      hackathonAttivo.getDataInizio().isAfter(LocalDateTime.now())
    ) {
      throw new BusinessLogicException(
        "L'hackathon '" +
        hackathonAttivo.getNome() +
        "' non è ancora iniziato. Le sottomissioni apriranno il " +
        hackathonAttivo.getDataInizio() +
        "."
      );
    }
    if (
      hackathonAttivo.getDataFine() != null &&
      hackathonAttivo.getDataFine().isBefore(LocalDateTime.now())
    ) {
      throw new BusinessLogicException(
        "L'hackathon '" +
        hackathonAttivo.getNome() +
        "' è già terminato il " +
        hackathonAttivo.getDataFine() +
        ". Non è più possibile inviare sottomissioni."
      );
    }

    Sottomissione sottomissione = sottomissioneDtoMapper.toEntity(dto);
    Sottomissione creata = sottomissioneHandler.inviaSottomissione(
      sottomissione
    );
    List<Valutazione> valutazioni = valutazioneHandler.ottieniValutazioniPerSottomissione(
      creata.getId()
    );
    return StandardResponse.success(
      sottomissioneDtoMapper.toResponseDTO(creata, valutazioni)
    );
  }

  /** Aggiorna sottomissione: solo membro del team della sottomissione (verifica in metodo). */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @PutMapping("/{sottomissioneId}")
  public StandardResponse<SottomissioneResponseDTO> aggiornaSottomissione(
    @PathVariable Long sottomissioneId,
    @Valid @RequestBody SottomissioneUpdateDTO dto
  )
    throws EntityNotFoundException, SubmissionDeadlinePassedException, InvalidGitHubLinkException {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    Sottomissione esistente = sottomissioneHandler.ottieniSottomissionePerId(
      sottomissioneId
    );
    AuthorizationUtils.requireTeamMember(
      utente,
      esistente.getTeamId(),
      teamRepository
    );

    Sottomissione aggiornata = new Sottomissione();
    aggiornata.setLinkProgetto(dto.getLinkProgetto());
    Sottomissione salvata = sottomissioneHandler.aggiornaSottomissione(
      sottomissioneId,
      aggiornata
    );
    List<Valutazione> valutazioni = valutazioneHandler.ottieniValutazioniPerSottomissione(
      salvata.getId()
    );
    return StandardResponse.success(
      sottomissioneDtoMapper.toResponseDTO(salvata, valutazioni)
    );
  }

  /** Sottomissioni per hackathon (autenticato). */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @GetMapping("/hackathon/{hackathonId}")
  public List<SottomissioneResponseDTO> ottieniSottomissioniPerHackathon(
    @PathVariable Long hackathonId
  ) {
    AuthorizationUtils.getCurrentUser(utenteRepository);
    List<Sottomissione> sottomissioni = sottomissioneHandler.ottieniSottomissioniPerHackathon(
      hackathonId
    );
    return sottomissioni
      .stream()
      .map(s ->
        sottomissioneDtoMapper.toResponseDTO(
          s,
          valutazioneHandler.ottieniValutazioniPerSottomissione(s.getId())
        )
      )
      .collect(Collectors.toList());
  }

  /** Sottomissioni per team (autenticato). */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @GetMapping("/team/{teamId}")
  public List<SottomissioneResponseDTO> ottieniSottomissioniPerTeam(
    @PathVariable Long teamId
  ) {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    //E' necessario verificare che l'utente sia membro del team
    //perche' altrimenti l'utente potrebbe vedere le sottomissioni di altri team
    AuthorizationUtils.requireTeamMember(utente, teamId, teamRepository);
    List<Sottomissione> sottomissioni = sottomissioneHandler.ottieniSottomissioniPerTeam(
      teamId
    );
    return sottomissioni
      .stream()
      .map(s ->
        sottomissioneDtoMapper.toResponseDTO(
          s,
          valutazioneHandler.ottieniValutazioniPerSottomissione(s.getId())
        )
      )
      .collect(Collectors.toList());
  }

  /** Sottomissioni da valutare per il giudice assegnato all'hackathon. */
  @RequiresRole(
    role = Utente.RuoloStaff.GIUDICE,
    requiresHackathonAssignment = true
  )
  @GetMapping("/hackathon/{hackathonId}/giudice/{giudiceUtenteId}")
  public List<SottomissioneResponseDTO> ottieniSottomissioniDaValutare(
    @PathVariable Long hackathonId,
    @PathVariable Long giudiceUtenteId
  ) {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    AuthorizationUtils.requireGiudiceOfHackathon(
      utente,
      hackathonId,
      hackathonRepository,
      staffHackatonRepository
    );
    List<Sottomissione> sottomissioni = sottomissioneHandler.ottieniSottomissioniPerHackathonEGiudice(
      hackathonId,
      utente.getId()
    );
    return sottomissioni
      .stream()
      .map(s ->
        sottomissioneDtoMapper.toResponseDTO(
          s,
          valutazioneHandler.ottieniValutazioniPerSottomissione(s.getId())
        )
      )
      .collect(Collectors.toList());
  }
}
