package it.hackhub.presentation.controllers.core;

import it.hackhub.application.dto.common.StandardResponse;
import it.hackhub.application.dto.valutazioni.ValutazioneCreateDTO;
import it.hackhub.application.dto.valutazioni.ValutazioneResponseDTO;
import it.hackhub.application.handlers.core.SottomissioneHandler;
import it.hackhub.application.handlers.core.ValutazioneHandler;
import it.hackhub.application.repositories.associations.StaffHackatonRepository;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Sottomissione;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.core.entities.core.Valutazione;
import it.hackhub.infrastructure.security.AuthorizationUtils;
import it.hackhub.infrastructure.security.annotations.RequiresRole;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

/**
 * Controller Valutazioni: logica e check allineati al riferimento.
 * Solo GIUDICE assegnato all'hackathon della sottomissione può creare valutazione; solo GIUDICE può vedere elenco.
 */
@RestController
@RequestMapping("/api/valutazioni")
public class ValutazioniController {

  private final ValutazioneHandler valutazioneHandler;
  private final SottomissioneHandler sottomissioneHandler;
  private final UtenteRepository utenteRepository;
  private final HackathonRepository hackathonRepository;
  private final StaffHackatonRepository staffHackatonRepository;

  public ValutazioniController(
    ValutazioneHandler valutazioneHandler,
    SottomissioneHandler sottomissioneHandler,
    UtenteRepository utenteRepository,
    HackathonRepository hackathonRepository,
    StaffHackatonRepository staffHackatonRepository
  ) {
    this.valutazioneHandler = valutazioneHandler;
    this.sottomissioneHandler = sottomissioneHandler;
    this.utenteRepository = utenteRepository;
    this.hackathonRepository = hackathonRepository;
    this.staffHackatonRepository = staffHackatonRepository;
  }

  @RequiresRole(role = Utente.RuoloStaff.GIUDICE)
  @GetMapping
  public List<Valutazione> ottieniTutteLeValutazioni() {
    AuthorizationUtils.getCurrentUser(utenteRepository);
    return valutazioneHandler.ottieniTutteLeValutazioni();
  }

  @RequiresRole(role = Utente.RuoloStaff.GIUDICE)
  @PostMapping("/crea")
  public StandardResponse<ValutazioneResponseDTO> aggiungiValutazione(
    @Valid @RequestBody ValutazioneCreateDTO dto
  ) {
    Utente utente = AuthorizationUtils.getCurrentUser(utenteRepository);
    Sottomissione sottomissione = sottomissioneHandler.ottieniSottomissionePerId(dto.getSottomissioneId());
    Long hackathonId = sottomissione.getHackathonId();
    AuthorizationUtils.requireGiudiceOfHackathon(
      utente,
      hackathonId,
      hackathonRepository,
      staffHackatonRepository
    );
    dto.setGiudiceId(utente.getId());
    ValutazioneResponseDTO created = valutazioneHandler.creaValutazione(dto);
    return StandardResponse.success(created);
  }
}
