package it.hackhub.application.dto;

/**
 * DTO per la creazione di una nuova Sottomissione.
 */
public class SottomissioneCreateDTO {

    private Long teamId;
    private Long hackathonId;
    private String linkProgetto;

    public SottomissioneCreateDTO() {}

    public SottomissioneCreateDTO(Long teamId, Long hackathonId, String linkProgetto) {
        this.teamId = teamId;
        this.hackathonId = hackathonId;
        this.linkProgetto = linkProgetto;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getHackathonId() {
        return hackathonId;
    }

    public void setHackathonId(Long hackathonId) {
        this.hackathonId = hackathonId;
    }

    public String getLinkProgetto() {
        return linkProgetto;
    }

    public void setLinkProgetto(String linkProgetto) {
        this.linkProgetto = linkProgetto;
    }
}
