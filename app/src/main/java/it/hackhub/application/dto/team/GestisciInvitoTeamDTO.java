package it.hackhub.application.dto.team;

/**
 * DTO per gestire un invito team (accetta/rifiuta).
 */
public class GestisciInvitoTeamDTO {

  private String azione; // ACCETTA | RIFIUTA

  public String getAzione() {
    return azione;
  }

  public void setAzione(String azione) {
    this.azione = azione;
  }
}
