package it.hackhub.application.dto.calendar;

/**
 * Response con il link alla call proposta
 */
public class CreaAppuntamentoResponse {

  private String linkEvento;

  public CreaAppuntamentoResponse() {}

  public CreaAppuntamentoResponse(String linkEvento) {
    this.linkEvento = linkEvento;
  }


  public String getLinkEvento() {
    return linkEvento;
  }

  public void setLinkEvento(String linkEvento) {
    this.linkEvento = linkEvento;
  }
}
