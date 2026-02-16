package it.hackhub.presentation.controllers.core;

import it.hackhub.application.dto.valutazioni.ValutazioneCreateDTO;
import it.hackhub.application.dto.valutazioni.ValutazioneResponseDTO;
import it.hackhub.application.handlers.core.ValutazioneHandler;

/**
 * Controller per le valutazioni
 */
public class ValutazioniController {

  private final ValutazioneHandler valutazioneHandler;

  public ValutazioniController(ValutazioneHandler valutazioneHandler) {
    this.valutazioneHandler = valutazioneHandler;
  }

  public ValutazioneResponseDTO aggiungiValutazione(ValutazioneCreateDTO dto) {
    return valutazioneHandler.creaValutazione(dto);
  }
}
