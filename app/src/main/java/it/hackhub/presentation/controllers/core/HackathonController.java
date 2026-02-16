package it.hackhub.presentation.controllers.core;

import it.hackhub.application.dto.hackathon.HackathonCreateDTO;
import it.hackhub.application.dto.hackathon.HackathonResponseDTO;
import it.hackhub.application.handlers.core.HackathonHandler;

/**
 * Controller Hackathon
 */
public class HackathonController {

  private final HackathonHandler hackathonHandler;

  public HackathonController(HackathonHandler hackathonHandler) {
    this.hackathonHandler = hackathonHandler;
  }

  public HackathonResponseDTO creaHackathon(HackathonCreateDTO dto) {
    return hackathonHandler.creaHackathon(dto);
  }

  /** Avvio fase iscrizione (chiamato da HackathonScheduler). */
  public int avviaFaseIscrizione() {
    return hackathonHandler.avviaFaseIscrizione();
  }

  /** Conclusione fase iscrizione (chiamato da HackathonScheduler). */
  public int concludiFaseIscrizione() {
    return hackathonHandler.concludiFaseIscrizione();
  }

  /** Avvio fase svolgimento (chiamato da HackathonScheduler). */
  public int avviaFaseSvolgimento() {
    return hackathonHandler.avviaFaseSvolgimento();
  }

  /** Conclusione fase svolgimento (chiamato da HackathonScheduler). */
  public int concludiFaseSvolgimento() {
    return hackathonHandler.concludiFaseSvolgimento();
  }
}
