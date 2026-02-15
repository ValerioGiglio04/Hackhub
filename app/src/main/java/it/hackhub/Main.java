package it.hackhub;

import it.hackhub.application.dto.hackathon.HackathonCreateDTO;
import it.hackhub.application.handlers.core.HackathonHandler;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.infrastructure.persistence.StorageInMemoria;
import it.hackhub.infrastructure.persistence.impl.HackathonRepositoryImpl;
import it.hackhub.presentation.controllers.core.HackathonController;
import java.time.LocalDateTime;

/**
 * Entry point – wiring e verifica setup iterazione 1.
 */
public class Main {

  public static void main(String[] args) {
    StorageInMemoria storage = new StorageInMemoria();
    HackathonRepository hackathonRepository = new HackathonRepositoryImpl(
      storage
    );
    HackathonHandler hackathonHandler = new HackathonHandler(
      hackathonRepository
    );
    HackathonController hackathonController = new HackathonController(
      hackathonHandler
    );
    //Testiamo i controller per vedere se funzionano inizialmente
    System.out.println("=== HackHub App Iterazione 1 - Setup OK ===\n");

    HackathonCreateDTO dto = new HackathonCreateDTO();
    dto.setNome("Hackathon Demo Iterazione 1");
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
      "Stub creaHackathon: id=" +
      response.getId() +
      ", nome=" +
      response.getNome() +
      ", stato=" +
      response.getStato()
    );
    System.out.println("\n=== Fine setup ===");
  }
}
