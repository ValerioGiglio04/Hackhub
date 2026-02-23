package it.hackhub.presentation.controllers.external;

import it.hackhub.application.dto.calendar.CreaAppuntamentoRequest;
import it.hackhub.application.dto.calendar.CreaAppuntamentoResponse;
import it.hackhub.application.dto.common.ApiResponseFactory;
import it.hackhub.application.dto.common.StandardResponse;
import it.hackhub.application.exceptions.CalendarConflictException;
import it.hackhub.application.exceptions.PastDateException;
import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.repositories.associations.IscrizioneTeamHackathonRepository;
import it.hackhub.application.repositories.associations.StaffHackatonRepository;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.application.repositories.support.RichiestaSupportoRepository;
import it.hackhub.core.entities.associations.StaffHackaton;
import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.StatoHackathon;
import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.application.handlers.external.calendar.CalendarExternalService;
import it.hackhub.infrastructure.security.annotations.RequiresRole;
import it.hackhub.infrastructure.security.userdetails.CustomUserDetails;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller Calendar: propone call di supporto (Mentore).
 * Crea evento su Google Calendar e restituisce il link; opzionalmente salva il link sulla richiesta di supporto.
 */
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

  private final CalendarExternalService calendarExternalService;
  private final UtenteRepository utenteRepository;
  private final TeamRepository teamRepository;
  private final HackathonRepository hackathonRepository;
  private final RichiestaSupportoRepository richiestaSupportoRepository;
  private final StaffHackatonRepository staffHackatonRepository;
  private final IscrizioneTeamHackathonRepository iscrizioneTeamHackathonRepository;

  public CalendarController(
    CalendarExternalService calendarExternalService,
    UtenteRepository utenteRepository,
    TeamRepository teamRepository,
    HackathonRepository hackathonRepository,
    RichiestaSupportoRepository richiestaSupportoRepository,
    StaffHackatonRepository staffHackatonRepository,
    IscrizioneTeamHackathonRepository iscrizioneTeamHackathonRepository
  ) {
    this.calendarExternalService = calendarExternalService;
    this.utenteRepository = utenteRepository;
    this.teamRepository = teamRepository;
    this.hackathonRepository = hackathonRepository;
    this.richiestaSupportoRepository = richiestaSupportoRepository;
    this.staffHackatonRepository = staffHackatonRepository;
    this.iscrizioneTeamHackathonRepository = iscrizioneTeamHackathonRepository;
  }

  /** @requiresRole Richiede ruolo MENTORE assegnato all'hackathon specificato */
  @RequiresRole(role = Utente.RuoloStaff.MENTORE, requiresHackathonAssignment = true)
  @PostMapping("/proponi-chiamata")
  public StandardResponse<CreaAppuntamentoResponse> creaAppuntamento(
    @RequestBody CreaAppuntamentoRequest request,
    Authentication authentication
  ) {
    if (request.getDataChiamata() == null || request.getOrario() == null) {
      throw new BusinessLogicException(
        "dataChiamata e orario sono obbligatori"
      );
    }

    Long currentUserId = null;
    if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
      currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getUtente().getId();
    }
    if (currentUserId != null && !currentUserId.equals(request.getIdMentore())) {
      throw new BusinessLogicException(
        "Solo il mentore indicato nella richiesta può proporre la chiamata"
      );
    }

    Utente mentore = utenteRepository
      .findById(request.getIdMentore())
      .orElseThrow(() ->
        new EntityNotFoundException("Mentore", request.getIdMentore())
      );

    Team team = teamRepository
      .findById(request.getIdTeam())
      .orElseThrow(() ->
        new EntityNotFoundException("Team", request.getIdTeam())
      );

    Hackathon hackathon = hackathonRepository
      .findById(request.getIdHackaton())
      .orElseThrow(() ->
        new EntityNotFoundException("Hackathon", request.getIdHackaton())
      );

    if (staffHackatonRepository != null) {
      List<StaffHackaton> staff = staffHackatonRepository.findByHackathonId(hackathon.getId());
      boolean mentoreAssegnato = staff
        .stream()
        .anyMatch(sh ->
          sh.getUtente() != null &&
          sh.getUtente().getId().equals(request.getIdMentore()) &&
          sh.getUtente().getRuolo() == Utente.RuoloStaff.MENTORE
        );
      if (!mentoreAssegnato) {
        throw new BusinessLogicException(
          "Il mentore '" +
            mentore.getNome() +
            " " +
            mentore.getCognome() +
            "' non è assegnato all'hackathon '" +
            hackathon.getNome() +
            "'. Solo mentori assegnati possono creare appuntamenti."
        );
      }
    }

    if (iscrizioneTeamHackathonRepository != null) {
      if (
        iscrizioneTeamHackathonRepository
          .findByTeamIdAndHackathonId(request.getIdTeam(), request.getIdHackaton())
          .isEmpty()
      ) {
        throw new BusinessLogicException(
          "Il team '" +
            team.getNome() +
            "' non è iscritto all'hackathon '" +
            hackathon.getNome() +
            "'. Non è possibile creare appuntamenti per team non iscritti."
        );
      }
    }

    if (hackathon.getStato() != StatoHackathon.IN_CORSO && hackathon.getStato() != StatoHackathon.IN_SVOLGIMENTO) {
      throw new BusinessLogicException(
        "L'hackathon '" +
          hackathon.getNome() +
          "' non è attivo. È possibile creare appuntamenti solo per hackathon in corso o in svolgimento."
      );
    }

    LocalDateTime requestDateTime = request.getDataChiamata().atTime(
      request.getOrario()
    );
    if (requestDateTime.isBefore(LocalDateTime.now())) {
      throw new BusinessLogicException(
        "La data/ora della chiamata (" +
          requestDateTime +
          ") è nel passato. È possibile creare appuntamenti solo per momenti futuri."
      );
    }

    if (
      hackathon.getDataInizio() != null &&
      requestDateTime.isBefore(hackathon.getDataInizio())
    ) {
      throw new BusinessLogicException(
        "Non è possibile creare la chiamata: la data/ora proposta non è nel range dell'hackathon. " +
          "Data/ora proposta: " + requestDateTime + ". " +
          "Periodo hackathon: da " + hackathon.getDataInizio() + " a " + hackathon.getDataFine() + "."
      );
    }
    if (
      hackathon.getDataFine() != null &&
      requestDateTime.isAfter(hackathon.getDataFine())
    ) {
      throw new BusinessLogicException(
        "Non è possibile creare la chiamata: la data/ora proposta non è nel range dell'hackathon. " +
          "Data/ora proposta: " + requestDateTime + ". " +
          "Periodo hackathon: da " + hackathon.getDataInizio() + " a " + hackathon.getDataFine() + "."
      );
    }

    if (
      request.getOrario().isBefore(LocalTime.of(9, 0)) ||
      request.getOrario().isAfter(LocalTime.of(18, 0))
    ) {
      throw new BusinessLogicException(
        "L'orario della chiamata (" +
          request.getOrario() +
          ") è fuori dagli orari di lavoro (9:00-18:00). Scegliere un orario valido."
      );
    }

    String titoloEvento =
      "Call Mentore - Team " +
      request.getIdTeam() +
      " - Hackathon " +
      request.getIdHackaton();
    String descrizione =
      "Chiamata di supporto tra il mentore (ID: " +
      request.getIdMentore() +
      ") e il team " +
      request.getIdTeam() +
      " per l'hackathon " +
      request.getIdHackaton() +
      ". Data: " +
      request.getDataChiamata() +
      ", Orario: " +
      request.getOrario();

    String linkEventoCalendar = calendarExternalService.creaEventoVideoconferenza(
      titoloEvento,
      descrizione,
      request.getDataChiamata(),
      request.getOrario(),
      "mentore@" + request.getIdMentore() + ".hackhub.com"
    );

    if (request.getRichiestaSupportoId() != null) {
      richiestaSupportoRepository
        .findById(request.getRichiestaSupportoId())
        .ifPresent(richiesta -> {
          richiesta.setLinkCallProposto(linkEventoCalendar);
          richiestaSupportoRepository.save(richiesta);
        });
    }

    CreaAppuntamentoResponse response = new CreaAppuntamentoResponse(
      linkEventoCalendar
    );
    return StandardResponse.success(response);
  }

  @ExceptionHandler(CalendarConflictException.class)
  public ResponseEntity<StandardResponse<Void>> handleCalendarConflict(
    CalendarConflictException e
  ) {
    return ApiResponseFactory.error(HttpStatus.CONFLICT, e.getMessage());
  }

  @ExceptionHandler(PastDateException.class)
  public ResponseEntity<StandardResponse<Void>> handlePastDate(
    PastDateException e
  ) {
    return ApiResponseFactory.error(HttpStatus.BAD_REQUEST, e.getMessage());
  }
}
