package it.hackhub.presentation.controllers.core;

import it.hackhub.application.dto.SottomissioneCreateDTO;
import it.hackhub.application.dto.SottomissioneUpdateDTO;
import it.hackhub.application.dto.common.StandardResponse;
import it.hackhub.application.handlers.core.SottomissioneHandler;
import it.hackhub.application.repositories.core.SottomissioneRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Sottomissione;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

/**
 * Controller Sottomissioni: Invia/Aggiorna sottomissione, Visualizza per hackathon/team.
 */
@RestController
@RequestMapping("/api/sottomissioni")
public class SottomissioniController {

  private final SottomissioneHandler sottomissioneHandler;
  private final SottomissioneRepository sottomissioneRepository;
  private final UtenteRepository utenteRepository;

  public SottomissioniController(
    SottomissioneHandler sottomissioneHandler,
    SottomissioneRepository sottomissioneRepository,
    UtenteRepository utenteRepository
  ) {
    this.sottomissioneHandler = sottomissioneHandler;
    this.sottomissioneRepository = sottomissioneRepository;
    this.utenteRepository = utenteRepository;
  }

  @GetMapping
  public List<Sottomissione> ottieniTutteLeSottomissioni() {
    SecurityUtils.getCurrentUserId(utenteRepository);
    return sottomissioneRepository.findAll();
  }

  @PostMapping("/invia")
  public StandardResponse<Sottomissione> inviaSottomissione(@Valid @RequestBody SottomissioneCreateDTO dto) {
    Long utenteId = SecurityUtils.getCurrentUserId(utenteRepository);
    Utente utente = new Utente();
    utente.setId(utenteId);
    Sottomissione creata = sottomissioneHandler.inviaSottomissione(dto, utente);
    return StandardResponse.success(creata);
  }

  @PutMapping("/{sottomissioneId}")
  public StandardResponse<Sottomissione> aggiornaSottomissione(
    @PathVariable Long sottomissioneId,
    @Valid @RequestBody SottomissioneUpdateDTO dto
  ) {
    Long utenteId = SecurityUtils.getCurrentUserId(utenteRepository);
    Utente utente = new Utente();
    utente.setId(utenteId);
    Sottomissione aggiornata = sottomissioneHandler.aggiornaSottomissione(sottomissioneId, dto, utente);
    return StandardResponse.success(aggiornata);
  }

  @GetMapping("/hackathon/{hackathonId}")
  public List<Sottomissione> ottieniSottomissioniPerHackathon(@PathVariable Long hackathonId) {
    SecurityUtils.getCurrentUserId(utenteRepository);
    return sottomissioneHandler.ottieniSottomissioniPerHackathon(hackathonId);
  }

  @GetMapping("/team/{teamId}")
  public List<Sottomissione> ottieniSottomissioniPerTeam(@PathVariable Long teamId) {
    SecurityUtils.getCurrentUserId(utenteRepository);
    return sottomissioneRepository.findByTeamId(teamId);
  }
}
