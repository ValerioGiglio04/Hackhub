package it.hackhub.presentation.controllers.external;

import it.hackhub.application.dto.calendar.CreaAppuntamentoRequest;
import it.hackhub.application.dto.calendar.CreaAppuntamentoResponse;
import it.hackhub.application.dto.common.StandardResponse;
import it.hackhub.application.repositories.support.RichiestaSupportoRepository;
import it.hackhub.core.entities.support.RichiestaSupporto;
import java.util.Optional;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller Calendar: Propone call di supporto (Mentore).
 * Crea evento (stub: link fittizio); opzionalmente salva il link sulla richiesta di supporto.
 */
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

  private final RichiestaSupportoRepository richiestaSupportoRepository;

  public CalendarController(
    RichiestaSupportoRepository richiestaSupportoRepository
  ) {
    this.richiestaSupportoRepository = richiestaSupportoRepository;
  }

  @PostMapping("/proponi-chiamata")
  public StandardResponse<CreaAppuntamentoResponse> creaAppuntamento(
    @RequestBody CreaAppuntamentoRequest request
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
    return StandardResponse.success(new CreaAppuntamentoResponse(link));
  }
}
