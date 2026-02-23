package it.hackhub.application.scheduler;

import it.hackhub.presentation.controllers.core.HackathonController;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler per le transizioni di stato degli hackathon (attore Tempo).
 */
@Component
public class HackathonScheduler {

  private final HackathonController hackathonController;

  public HackathonScheduler(HackathonController hackathonController) {
    this.hackathonController = hackathonController;
  }

  @Scheduled(fixedDelay = 60000)
  public void updateHackathonStates() {
    hackathonController.avviaFaseIscrizione();
    hackathonController.concludiFaseIscrizione();
    hackathonController.avviaFaseSvolgimento();
    hackathonController.concludiFaseSvolgimento();
  }
}
