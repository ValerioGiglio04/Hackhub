package it.hackhub;

import it.hackhub.application.dto.hackathon.HackathonCreateDTO;
import it.hackhub.application.handlers.core.HackathonHandler;
import it.hackhub.application.handlers.core.ValutazioneHandler;
import it.hackhub.application.handlers.support.SupportHandler;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.SottomissioneRepository;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.ValutazioneRepository;
import it.hackhub.application.repositories.support.RichiestaSupportoRepository;
import it.hackhub.application.repositories.support.SegnalazioneViolazioneRepository;
import it.hackhub.application.scheduler.HackathonScheduler;
import it.hackhub.infrastructure.persistence.StorageInMemoria;
import it.hackhub.infrastructure.persistence.impl.HackathonRepositoryImpl;
import it.hackhub.infrastructure.persistence.impl.RichiestaSupportoRepositoryImpl;
import it.hackhub.infrastructure.persistence.impl.SegnalazioneViolazioneRepositoryImpl;
import it.hackhub.infrastructure.persistence.impl.SottomissioneRepositoryImpl;
import it.hackhub.infrastructure.persistence.impl.TeamRepositoryImpl;
import it.hackhub.infrastructure.persistence.impl.ValutazioneRepositoryImpl;
import it.hackhub.presentation.controllers.core.HackathonController;
import it.hackhub.presentation.controllers.core.ValutazioniController;
import it.hackhub.presentation.controllers.external.CalendarController;
import it.hackhub.presentation.controllers.support.SupportController;
import java.time.LocalDateTime;

/**
 * Entry point
 */
public class Main {

  public static void main(String[] args) {
    StorageInMemoria storage = new StorageInMemoria();

    HackathonRepository hackathonRepository = new HackathonRepositoryImpl(
      storage
    );
    TeamRepository teamRepository = new TeamRepositoryImpl(storage);
    SottomissioneRepository sottomissioneRepository = new SottomissioneRepositoryImpl(
      storage
    );
    ValutazioneRepository valutazioneRepository = new ValutazioneRepositoryImpl(
      storage
    );
    RichiestaSupportoRepository richiestaSupportoRepository = new RichiestaSupportoRepositoryImpl(
      storage
    );
    SegnalazioneViolazioneRepository segnalazioneViolazioneRepository = new SegnalazioneViolazioneRepositoryImpl(
      storage
    );

    HackathonHandler hackathonHandler = new HackathonHandler(
      hackathonRepository,
      teamRepository,
      sottomissioneRepository,
      valutazioneRepository
    );
    HackathonController hackathonController = new HackathonController(
      hackathonHandler
    );
    HackathonScheduler hackathonScheduler = new HackathonScheduler(
      hackathonController
    );

    ValutazioneHandler valutazioneHandler = new ValutazioneHandler(
      valutazioneRepository
    );
    ValutazioniController valutazioniController = new ValutazioniController(
      valutazioneHandler
    );

    SupportHandler supportHandler = new SupportHandler(
      richiestaSupportoRepository,
      segnalazioneViolazioneRepository
    );
    SupportController supportController = new SupportController(supportHandler);

    CalendarController calendarController = new CalendarController(
      richiestaSupportoRepository
    );

    System.out.println("=== HackHub App - Setup OK ===\n");

    HackathonCreateDTO dto = new HackathonCreateDTO();
    dto.setNome("Hackathon Demo");
    dto.setRegolamento("Regolamento esempio.");
    LocalDateTime now = LocalDateTime.now();
    dto.setInizioIscrizioni(now.plusDays(1));
    dto.setScadenzaIscrizioni(now.plusDays(5));
    dto.setDataInizio(now.plusDays(7));
    dto.setDataFine(now.plusDays(8));
    dto.setScadenzaSottomissioni(now.plusDays(8));
    dto.setLuogo("Roma");
    dto.setPremio(1000.0);
    dto.setMaxTeamSize(5);

    var response = hackathonController.creaHackathon(dto);
    System.out.println(
      "CreaHackathon: id=" +
      response.getId() +
      ", nome=" +
      response.getNome() +
      ", stato=" +
      response.getStato()
    );

    hackathonScheduler.updateHackathonStates();
    System.out.println("HackathonScheduler.updateHackathonStates() eseguito.");

    System.out.println("\n=== Fine setup ===");
  }
}
