package it.hackhub.core.builders.core;

import it.hackhub.core.entities.core.Valutazione;

/**
 * Builder per la classe Valutazione (usa sottomissioneId e giudiceId).
 */
public class ValutazioneBuilder {

  private Long sottomissioneId;
  private Long giudiceId;
  private Integer punteggio;
  private String commento;

  public ValutazioneBuilder sottomissioneId(Long sottomissioneId) {
    this.sottomissioneId = sottomissioneId;
    return this;
  }

  public ValutazioneBuilder giudiceId(Long giudiceId) {
    this.giudiceId = giudiceId;
    return this;
  }

  public ValutazioneBuilder punteggio(Integer punteggio) {
    this.punteggio = punteggio;
    return this;
  }

  public ValutazioneBuilder commento(String commento) {
    this.commento = commento;
    return this;
  }

  public Valutazione build() {
    if (sottomissioneId == null) {
      throw new IllegalArgumentException("La sottomissione è obbligatoria");
    }
    if (giudiceId == null) {
      throw new IllegalArgumentException("Il giudice è obbligatorio");
    }
    if (punteggio == null || punteggio < 0 || punteggio > 10) {
      throw new IllegalArgumentException("Il punteggio deve essere tra 0 e 10");
    }
    Valutazione v = new Valutazione();
    v.setSottomissioneId(sottomissioneId);
    v.setGiudiceId(giudiceId);
    v.setPunteggio(punteggio);
    v.setCommento(commento);
    return v;
  }
}
