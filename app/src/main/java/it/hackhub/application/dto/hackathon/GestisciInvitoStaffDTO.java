package it.hackhub.application.dto.hackathon;

/**
 * DTO per gestire un invito staff (accetta/rifiuta).
 */
public class GestisciInvitoStaffDTO {

  private String azione; // ACCETTA | RIFIUTA

  public String getAzione() {
    return azione;
  }

  public void setAzione(String azione) {
    this.azione = azione;
  }
}
