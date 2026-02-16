package it.hackhub.presentation.controllers.support;

import it.hackhub.application.dto.supporto.RichiestaSupportoCreateDTO;
import it.hackhub.application.dto.supporto.RichiestaSupportoResponseDTO;
import it.hackhub.application.dto.supporto.SegnalazioneViolazioneCreateDTO;
import it.hackhub.application.handlers.support.SupportHandler;
import java.util.List;

/**
 * Controller Support: Visualizza richieste supporto, Segnala violazioni, Crea richiesta supporto.
 */
public class SupportController {

  private final SupportHandler supportHandler;

  public SupportController(SupportHandler supportHandler) {
    this.supportHandler = supportHandler;
  }

  public RichiestaSupportoResponseDTO creaRichiestaSupporto(
    RichiestaSupportoCreateDTO dto
  ) {
    return supportHandler.creaRichiestaSupporto(dto);
  }

  public List<RichiestaSupportoResponseDTO> visualizzaRichiesteSupporto() {
    return supportHandler.ottieniRichiesteSupporto();
  }

  public void segnalaViolazione(SegnalazioneViolazioneCreateDTO dto) {
    supportHandler.segnalaViolazione(dto);
  }
}
