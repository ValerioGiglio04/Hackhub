package it.hackhub.application.dto;

/**
 * DTO per l'aggiornamento di una Sottomissione esistente.
 */
public class SottomissioneUpdateDTO {

    private String linkProgetto;

    public SottomissioneUpdateDTO() {}

    public SottomissioneUpdateDTO(String linkProgetto) {
        this.linkProgetto = linkProgetto;
    }

    public String getLinkProgetto() {
        return linkProgetto;
    }

    public void setLinkProgetto(String linkProgetto) {
        this.linkProgetto = linkProgetto;
    }
}
