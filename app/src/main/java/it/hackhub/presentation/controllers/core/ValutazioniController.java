package it.hackhub.presentation.controllers.core;

import it.hackhub.application.dto.common.StandardResponse;
import it.hackhub.application.dto.valutazioni.ValutazioneCreateDTO;
import it.hackhub.application.dto.valutazioni.ValutazioneResponseDTO;
import it.hackhub.application.handlers.core.ValutazioneHandler;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.application.repositories.core.ValutazioneRepository;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.core.entities.core.Valutazione;
import it.hackhub.infrastructure.security.SecurityUtils;
import it.hackhub.infrastructure.security.annotations.RequiresRole;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

/**
 * Controller Valutazioni: Crea valutazione, Visualizza valutazioni. Ruolo GIUDICE.
 */
@RestController
@RequestMapping("/api/valutazioni")
public class ValutazioniController {

  private final ValutazioneHandler valutazioneHandler;
  private final ValutazioneRepository valutazioneRepository;
  private final UtenteRepository utenteRepository;

  public ValutazioniController(
    ValutazioneHandler valutazioneHandler,
    ValutazioneRepository valutazioneRepository,
    UtenteRepository utenteRepository
  ) {
    this.valutazioneHandler = valutazioneHandler;
    this.valutazioneRepository = valutazioneRepository;
    this.utenteRepository = utenteRepository;
  }

  /** @requiresRole Richiede autenticazione (qualsiasi ruolo) */
  @RequiresRole(role = Utente.RuoloStaff.AUTENTICATO)
  @GetMapping
  public List<Valutazione> ottieniTutteLeValutazioni() {
    SecurityUtils.getCurrentUserId(utenteRepository);
    return valutazioneRepository.findAll();
  }

  /** @requiresRole Richiede ruolo GIUDICE assegnato all'hackathon specificato */
  @RequiresRole(role = Utente.RuoloStaff.GIUDICE, requiresHackathonAssignment = true)
  @PostMapping("/crea")
  public StandardResponse<ValutazioneResponseDTO> aggiungiValutazione(
    @Valid @RequestBody ValutazioneCreateDTO dto
  ) {
    Long giudiceId = SecurityUtils.getCurrentUserId(utenteRepository);
    dto.setGiudiceId(giudiceId);
    ValutazioneResponseDTO created = valutazioneHandler.creaValutazione(dto);
    return StandardResponse.success(created);
  }
}
