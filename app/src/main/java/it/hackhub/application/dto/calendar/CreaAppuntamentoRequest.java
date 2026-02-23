package it.hackhub.application.dto.calendar;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request per proporre una call di supporto
 */
public class CreaAppuntamentoRequest {

  private Long idTeam;
  private Long idHackaton;
  private Long idMentore;
  private Long richiestaSupportoId;
  private LocalDate dataChiamata;
  private LocalTime orario;

  public Long getIdTeam() {
    return idTeam;
  }

  public void setIdTeam(Long idTeam) {
    this.idTeam = idTeam;
  }

  public Long getIdHackaton() {
    return idHackaton;
  }

  /** Usato dalla sicurezza (RequiresRoleAspect) per verificare assegnazione mentore all'hackathon. */
  public Long getHackathonId() {
    return idHackaton;
  }

  public void setIdHackaton(Long idHackaton) {
    this.idHackaton = idHackaton;
  }

  public Long getIdMentore() {
    return idMentore;
  }

  public void setIdMentore(Long idMentore) {
    this.idMentore = idMentore;
  }

  public Long getRichiestaSupportoId() {
    return richiestaSupportoId;
  }

  public void setRichiestaSupportoId(Long richiestaSupportoId) {
    this.richiestaSupportoId = richiestaSupportoId;
  }

  public LocalDate getDataChiamata() {
    return dataChiamata;
  }

  public void setDataChiamata(LocalDate dataChiamata) {
    this.dataChiamata = dataChiamata;
  }

  public LocalTime getOrario() {
    return orario;
  }

  public void setOrario(LocalTime orario) {
    this.orario = orario;
  }
}
