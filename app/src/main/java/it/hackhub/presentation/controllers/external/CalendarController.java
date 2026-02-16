package it.hackhub.presentation.controllers.external;

import it.hackhub.application.dto.calendar.CreaAppuntamentoRequest;
import it.hackhub.application.dto.calendar.CreaAppuntamentoResponse;
import it.hackhub.application.repositories.support.RichiestaSupportoRepository;
import it.hackhub.core.entities.support.RichiestaSupporto;
import java.util.Optional;

/**
 * Controller Calendar: Propone call di supporto.
 * Stub: restituisce un link fittizio; opzionalmente aggiorna la richiesta di supporto con il link.
 */
public class CalendarController {

  private final RichiestaSupportoRepository richiestaSupportoRepository;

  public CalendarController(
    RichiestaSupportoRepository richiestaSupportoRepository
  ) {
    this.richiestaSupportoRepository = richiestaSupportoRepository;
  }

  public CreaAppuntamentoResponse creaAppuntamento(
    CreaAppuntamentoRequest request
  ) {
    String link =
      "https://meet.example.com/call-" +
      request.getIdHackaton() +
      "-" +
      request.getIdTeam();
    if (request.getRichiestaSupportoId() != null) {
      Optional<RichiestaSupporto> opt = richiestaSupportoRepository.findById(
        request.getRichiestaSupportoId()
      );
      if (opt.isPresent()) {
        RichiestaSupporto r = opt.get();
        r.setLinkCallProposto(link);
        richiestaSupportoRepository.save(r);
      }
    }
    return new CreaAppuntamentoResponse(link);
  }
}
