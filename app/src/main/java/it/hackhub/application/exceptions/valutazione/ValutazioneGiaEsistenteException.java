package it.hackhub.application.exceptions.valutazione;

public class ValutazioneGiaEsistenteException extends RuntimeException {

  public ValutazioneGiaEsistenteException(Long sottomissioneId, Long giudiceId) {
    super("Valutazione già presente per sottomissione " + sottomissioneId + " e giudice " + giudiceId);
  }
}
