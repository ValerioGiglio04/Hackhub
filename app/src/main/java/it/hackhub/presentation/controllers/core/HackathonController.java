package it.hackhub.presentation.controllers.core;

import it.hackhub.application.dto.hackathon.HackathonCreateDTO;
import it.hackhub.application.dto.hackathon.HackathonResponseDTO;
import it.hackhub.application.handlers.core.HackathonHandler;

/**
 * Controller Hackathon – iterazione 0: solo creaHackathon
 */
public class HackathonController {

  private final HackathonHandler hackathonHandler;

  public HackathonController(HackathonHandler hackathonHandler) {
    this.hackathonHandler = hackathonHandler;
  }

  public HackathonResponseDTO creaHackathon(HackathonCreateDTO dto) {
    return hackathonHandler.creaHackathon(dto);
  }
}
