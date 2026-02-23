package it.hackhub.presentation.controllers;

import it.hackhub.application.dto.common.StandardResponse;
import it.hackhub.application.dto.hackathon.GestisciInvitoStaffDTO;
import it.hackhub.application.dto.hackathon.InvitoStaffResponseDTO;
import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.application.handlers.InvitiStaffHandler;
import it.hackhub.application.mappers.InvitoStaffDtoMapper;
import it.hackhub.application.repositories.associations.StaffHackatonRepository;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.associations.InvitoStaff;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.infrastructure.security.AuthorizationUtils;
import it.hackhub.infrastructure.security.annotations.RequiresRole;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;

/**
 * Controller Inviti staff hackathon: logica e check allineati al progetto di riferimento.
 * Solo ORGANIZZATORE/MENTORE/GIUDICE vedono inviti ricevuti; solo organizzatore dell'hackathon vede inviti per hackathon.
 */
@RestController
@RequestMapping("/api/staff-inviti")
public class InvitiStaffController {

  private final InvitiStaffHandler invitiStaffHandler;
  private final UtenteRepository utenteRepository;
  private final HackathonRepository hackathonRepository;
  private final StaffHackatonRepository staffHackatonRepository;

  public InvitiStaffController(
    InvitiStaffHandler invitiStaffHandler,
    UtenteRepository utenteRepository,
    HackathonRepository hackathonRepository,
    StaffHackatonRepository staffHackatonRepository
  ) {
    this.invitiStaffHandler = invitiStaffHandler;
    this.utenteRepository = utenteRepository;
    this.hackathonRepository = hackathonRepository;
    this.staffHackatonRepository = staffHackatonRepository;
  }

  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @GetMapping("/ricevuti")
  public List<InvitoStaffResponseDTO> ottieniInvitiRicevuti() {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    List<Utente.RuoloStaff> ruoliPermessi = List.of(
      Utente.RuoloStaff.ORGANIZZATORE,
      Utente.RuoloStaff.MENTORE,
      Utente.RuoloStaff.GIUDICE
    );
    if (!ruoliPermessi.contains(utente.getRuolo())) {
      throw new UnauthorizedException(
        "Solo i membri dello staff con ruolo organizzatore, mentore o giudice possono vedere gli inviti ricevuti."
      );
    }
    return invitiStaffHandler
      .ottieniInvitiRicevutiPending(utente.getId())
      .stream()
      .map(InvitoStaffDtoMapper::toResponseDTO)
      .collect(Collectors.toList());
  }

  @RequiresRole(role = Utente.RuoloStaff.ORGANIZZATORE)
  @GetMapping("/hackathon/{hackathonId}")
  public List<InvitoStaffResponseDTO> ottieniInvitiPendingByHackathon(
    @PathVariable Long hackathonId
  ) {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    hackathonRepository
      .findById(hackathonId)
      .orElseThrow(() -> new EntityNotFoundException("Hackathon", hackathonId));
    boolean isOrganizzatore = staffHackatonRepository.findByHackathonId(hackathonId).stream()
      .anyMatch(sh -> sh.getUtente() != null
        && sh.getUtente().getId().equals(utente.getId())
        && sh.getUtente().getRuolo() == Utente.RuoloStaff.ORGANIZZATORE);
    if (!isOrganizzatore) {
      throw new UnauthorizedException(
        "Solo un organizzatore assegnato a questo hackathon può vedere gli inviti."
      );
    }
    return invitiStaffHandler
      .ottieniInvitiPendingByHackathon(hackathonId)
      .stream()
      .map(InvitoStaffDtoMapper::toResponseDTO)
      .collect(Collectors.toList());
  }

  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @PostMapping("/{invitoId}/gestisci-invito")
  public StandardResponse<?> gestisciInvito(
    @PathVariable Long invitoId,
    @Valid @RequestBody GestisciInvitoStaffDTO dto
  ) {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    if (dto.getAzione() == null || dto.getAzione().isBlank()) {
      throw new BusinessLogicException("Azione non valida: " + dto.getAzione());
    }
    switch (dto.getAzione().toUpperCase()) {
      case "ACCETTA":
        invitiStaffHandler.accettaInvitoStaff(invitoId, utente.getId());
        return StandardResponse.success("Invito accettato con successo");
      case "RIFIUTA":
        InvitoStaff invito = invitiStaffHandler.rifiutaInvitoStaff(invitoId, utente.getId());
        return StandardResponse.success(InvitoStaffDtoMapper.toResponseDTO(invito));
      default:
        throw new BusinessLogicException("Azione non valida: " + dto.getAzione());
    }
  }
}
