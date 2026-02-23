package it.hackhub.application.handlers.core;

import it.hackhub.application.dto.valutazioni.ValutazioneCreateDTO;
import it.hackhub.application.dto.valutazioni.ValutazioneResponseDTO;
import it.hackhub.application.exceptions.valutazione.ValutazioneGiaEsistenteException;
import it.hackhub.application.repositories.core.ValutazioneRepository;
import it.hackhub.core.entities.core.Valutazione;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

/**
 * Handler per il caso d'uso Valuta sottomissione
 */
@Service
public class ValutazioneHandler {

  private final ValutazioneRepository valutazioneRepository;

  public ValutazioneHandler(ValutazioneRepository valutazioneRepository) {
    this.valutazioneRepository = valutazioneRepository;
  }

  public ValutazioneResponseDTO creaValutazione(ValutazioneCreateDTO dto) {
    if (
      valutazioneRepository
        .findBySottomissioneIdAndGiudiceId(
          dto.getSottomissioneId(),
          dto.getGiudiceId()
        )
        .isPresent()
    ) {
      throw new ValutazioneGiaEsistenteException(
        dto.getSottomissioneId(),
        dto.getGiudiceId()
      );
    }
    Valutazione v = new Valutazione();
    v.setSottomissioneId(dto.getSottomissioneId());
    v.setGiudiceId(dto.getGiudiceId());
    v.setPunteggio(dto.getPunteggio());
    v.setCommento(dto.getCommento());
    v.setDataValutazione(LocalDateTime.now());
    Valutazione saved = valutazioneRepository.save(v);
    ValutazioneResponseDTO res = new ValutazioneResponseDTO();
    res.setId(saved.getId());
    res.setSottomissioneId(saved.getSottomissioneId());
    res.setGiudiceId(saved.getGiudiceId());
    res.setPunteggio(saved.getPunteggio());
    res.setCommento(saved.getCommento());
    res.setDataValutazione(saved.getDataValutazione());
    return res;
  }
}
