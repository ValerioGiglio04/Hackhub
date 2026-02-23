package it.hackhub.application.dto.valutazioni;

/**
 * DTO per creare una valutazione (Use case: Valuta sottomissione).
 */
public class ValutazioneCreateDTO {

  private Long sottomissioneId;
  private Long hackathonId;
  private Long giudiceId;
  private Integer punteggio;
  private String commento;

  public Long getSottomissioneId() { return sottomissioneId; }
  public void setSottomissioneId(Long sottomissioneId) { this.sottomissioneId = sottomissioneId; }
  public Long getHackathonId() { return hackathonId; }
  public void setHackathonId(Long hackathonId) { this.hackathonId = hackathonId; }
  public Long getGiudiceId() { return giudiceId; }
  public void setGiudiceId(Long giudiceId) { this.giudiceId = giudiceId; }
  public Integer getPunteggio() { return punteggio; }
  public void setPunteggio(Integer punteggio) { this.punteggio = punteggio; }
  public String getCommento() { return commento; }
  public void setCommento(String commento) { this.commento = commento; }
}
