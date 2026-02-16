package it.hackhub.application.scheduler;

import it.hackhub.presentation.controllers.core.HackathonController;

/**
 * Scheduler per le transizioni di stato degli hackathon (attore Tempo).
 * Esegue avvio/conclusione fase iscrizione e avvio/conclusione fase svolgimento.
 */
public class HackathonScheduler {

  private final HackathonController hackathonController;

  public HackathonScheduler(HackathonController hackathonController) {
    this.hackathonController = hackathonController;
  }

  /**
   * Verifica e applica le transizioni di stato (da invocare periodicamente, es. ogni minuto).
   */
  public void updateHackathonStates() {
    hackathonController.avviaFaseIscrizione();
    hackathonController.concludiFaseIscrizione();
    hackathonController.avviaFaseSvolgimento();
    hackathonController.concludiFaseSvolgimento();
  }
}
