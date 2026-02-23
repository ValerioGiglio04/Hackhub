package it.hackhub.application.dto.valutazioni;

import it.hackhub.application.dto.hackathon.HackathonSummaryDTO;
import it.hackhub.application.dto.team.TeamSummaryDTO;
import java.time.LocalDateTime;

public class SottomissioneResponseDTO {

  private Long id;
  private TeamSummaryDTO team;
  private HackathonSummaryDTO hackathon;
  private String linkProgetto;
  private LocalDateTime dataCaricamento;
  private LocalDateTime dataUltimoUpdate;
  private Integer numeroValutazioni;
  private Double punteggioMedio;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public TeamSummaryDTO getTeam() {
    return team;
  }

  public void setTeam(TeamSummaryDTO team) {
    this.team = team;
  }

  public HackathonSummaryDTO getHackathon() {
    return hackathon;
  }

  public void setHackathon(HackathonSummaryDTO hackathon) {
    this.hackathon = hackathon;
  }

  public String getLinkProgetto() {
    return linkProgetto;
  }

  public void setLinkProgetto(String linkProgetto) {
    this.linkProgetto = linkProgetto;
  }

  public LocalDateTime getDataCaricamento() {
    return dataCaricamento;
  }

  public void setDataCaricamento(LocalDateTime dataCaricamento) {
    this.dataCaricamento = dataCaricamento;
  }

  public LocalDateTime getDataUltimoUpdate() {
    return dataUltimoUpdate;
  }

  public void setDataUltimoUpdate(LocalDateTime dataUltimoUpdate) {
    this.dataUltimoUpdate = dataUltimoUpdate;
  }

  public Integer getNumeroValutazioni() {
    return numeroValutazioni;
  }

  public void setNumeroValutazioni(Integer numeroValutazioni) {
    this.numeroValutazioni = numeroValutazioni;
  }

  public Double getPunteggioMedio() {
    return punteggioMedio;
  }

  public void setPunteggioMedio(Double punteggioMedio) {
    this.punteggioMedio = punteggioMedio;
  }
}
