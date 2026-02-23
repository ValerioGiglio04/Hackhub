package it.hackhub.application.dto.valutazioni;

import it.hackhub.application.dto.utente.UtenteSummaryDTO;
import java.time.LocalDateTime;

/**
 * DTO di risposta per una valutazione.
 */
public class ValutazioneResponseDTO {

  private Long id;
  private Long sottomissioneId;
  private Long giudiceId;
  private Integer punteggio;
  private String commento;
  private LocalDateTime dataValutazione;
  private SottomissioneSummaryDTO sottomissione;
  private UtenteSummaryDTO giudice;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getSottomissioneId() { return sottomissioneId; }
  public void setSottomissioneId(Long sottomissioneId) { this.sottomissioneId = sottomissioneId; }
  public Long getGiudiceId() { return giudiceId; }
  public void setGiudiceId(Long giudiceId) { this.giudiceId = giudiceId; }
  public Integer getPunteggio() { return punteggio; }
  public void setPunteggio(Integer punteggio) { this.punteggio = punteggio; }
  public String getCommento() { return commento; }
  public void setCommento(String commento) { this.commento = commento; }
  public LocalDateTime getDataValutazione() { return dataValutazione; }
  public void setDataValutazione(LocalDateTime dataValutazione) { this.dataValutazione = dataValutazione; }
  public SottomissioneSummaryDTO getSottomissione() { return sottomissione; }
  public void setSottomissione(SottomissioneSummaryDTO sottomissione) { this.sottomissione = sottomissione; }
  public UtenteSummaryDTO getGiudice() { return giudice; }
  public void setGiudice(UtenteSummaryDTO giudice) { this.giudice = giudice; }
}
