package it.hackhub.presentation.controllers.core;

import it.hackhub.application.dto.hackathon.HackathonCreateDTO;
import it.hackhub.application.dto.hackathon.HackathonResponseDTO;
import it.hackhub.application.dto.hackathon.HackathonUpdateDTO;
import it.hackhub.application.dto.hackathon.HackathonWinnerDTO;
import it.hackhub.application.handlers.core.HackathonHandler;
import it.hackhub.core.entities.core.Hackathon;

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

  public HackathonResponseDTO aggiornaHackathon(
    Long hackathonId,
    HackathonUpdateDTO dto
  ) {
    Hackathon h = hackathonHandler.aggiornaHackathon(hackathonId, dto);
    HackathonResponseDTO res = new HackathonResponseDTO();
    res.setId(h.getId());
    res.setNome(h.getNome());
    res.setStato(h.getStato());
    return res;
  }

  public void impostaVincitore(HackathonWinnerDTO dto) {
    hackathonHandler.impostaVincitore(dto.getHackathonId(), dto.getTeamId());
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
