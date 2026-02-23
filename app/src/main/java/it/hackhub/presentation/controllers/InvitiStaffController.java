package it.hackhub.presentation.controllers;

import it.hackhub.application.dto.hackathon.GestisciInvitoStaffDTO;
import it.hackhub.application.dto.hackathon.InvitoStaffResponseDTO;
import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.handlers.InvitiStaffHandler;
import it.hackhub.application.mappers.InvitoStaffDtoMapper;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.associations.InvitoStaff;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.infrastructure.security.SecurityUtils;
import it.hackhub.infrastructure.security.annotations.RequiresRole;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST per inviti staff (visualizza ricevuti, accetta/rifiuta).
 */
@RestController
@RequestMapping("/api/staff-inviti")
public class InvitiStaffController {

  private final InvitiStaffHandler invitiStaffHandler;
  private final UtenteRepository utenteRepository;

  public InvitiStaffController(
    InvitiStaffHandler invitiStaffHandler,
    UtenteRepository utenteRepository
  ) {
    this.invitiStaffHandler = invitiStaffHandler;
    this.utenteRepository = utenteRepository;
  }

  /** @requiresRole Richiede autenticazione (qualsiasi ruolo) */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @GetMapping("/ricevuti")
  public List<InvitoStaffResponseDTO> ottieniInvitiRicevuti() {
    Long utenteCorrenteId = SecurityUtils.getCurrentUserId(utenteRepository);
    return invitiStaffHandler
      .ottieniInvitiRicevutiPending(utenteCorrenteId)
      .stream()
      .map(InvitoStaffDtoMapper::toResponseDTO)
      .collect(Collectors.toList());
  }

  /** @requiresRole Richiede autenticazione (qualsiasi ruolo) */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @PostMapping("/{invitoId}/gestisci")
  public Object gestisciInvito(
    @PathVariable Long invitoId,
    @RequestBody GestisciInvitoStaffDTO dto
  ) {
    Long utenteCorrenteId = SecurityUtils.getCurrentUserId(utenteRepository);
    if (dto.getAzione() == null || dto.getAzione().isBlank()) {
      throw new BusinessLogicException("Azione non valida");
    }
    String azione = dto.getAzione().toUpperCase();
    switch (azione) {
      case "ACCETTA":
        invitiStaffHandler.accettaInvitoStaff(invitoId, utenteCorrenteId);
        return null;
      case "RIFIUTA":
        InvitoStaff invito = invitiStaffHandler.rifiutaInvitoStaff(
          invitoId,
          utenteCorrenteId
        );
        return InvitoStaffDtoMapper.toResponseDTO(invito);
      default:
        throw new BusinessLogicException(
          "Azione non valida: " + dto.getAzione()
        );
    }
  }
}
