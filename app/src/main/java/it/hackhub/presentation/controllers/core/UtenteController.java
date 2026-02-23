package it.hackhub.presentation.controllers.core;

import it.hackhub.application.dto.utente.UtenteResponseDTO;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.mappers.UtenteDtoMapper;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.infrastructure.security.SecurityUtils;
import it.hackhub.infrastructure.security.annotations.RequiresRole;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller Utenti: Visualizza elenco utenti e dettaglio per ID (Use case: Visualizza informazioni utente).
 */
@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

  private final UtenteRepository utenteRepository;

  public UtenteController(UtenteRepository utenteRepository) {
    this.utenteRepository = utenteRepository;
  }

  /** @requiresRole Richiede autenticazione (qualsiasi ruolo) */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @GetMapping
  public List<UtenteResponseDTO> ottieniTuttiGliUtenti() {
    SecurityUtils.getCurrentUserId(utenteRepository);
    return utenteRepository.findAll().stream()
      .map(UtenteDtoMapper::toResponseDTO)
      .collect(Collectors.toList());
  }

  /** @requiresRole Richiede autenticazione (qualsiasi ruolo) */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @GetMapping("/{utenteId}")
  public UtenteResponseDTO ottieniUtentePerId(@PathVariable Long utenteId) {
    SecurityUtils.getCurrentUserId(utenteRepository);
    Utente utente = utenteRepository.findById(utenteId)
      .orElseThrow(() -> new EntityNotFoundException("Utente", utenteId));
    return UtenteDtoMapper.toResponseDTO(utente);
  }
}
