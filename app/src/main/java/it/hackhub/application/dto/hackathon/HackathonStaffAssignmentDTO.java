package it.hackhub.application.dto.hackathon;

/**
 * DTO per invito di un utente staff a un hackathon (Organizzatore).
 */
public class HackathonStaffAssignmentDTO {

  private Long hackathonId;
  private Long utenteId;

  public Long getHackathonId() {
    return hackathonId;
  }

  public void setHackathonId(Long hackathonId) {
    this.hackathonId = hackathonId;
  }

  public Long getUtenteId() {
    return utenteId;
  }

  public void setUtenteId(Long utenteId) {
    this.utenteId = utenteId;
  }
}
