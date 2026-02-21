package it.hackhub.application.dto.hackathon;

import it.hackhub.core.entities.associations.InvitoStaff;
import java.time.LocalDateTime;

/**
 * DTO di risposta per un invito a far parte dello staff di un hackathon.
 */
public class InvitoStaffResponseDTO {

  private Long id;
  private Long hackathonId;
  private String nomeHackathon;
  private Long utenteInvitatoId;
  private String utenteInvitatoNome;
  private Long mittenteId;
  private InvitoStaff.StatoInvito stato;
  private LocalDateTime dataInvito;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getHackathonId() { return hackathonId; }
  public void setHackathonId(Long hackathonId) { this.hackathonId = hackathonId; }
  public String getNomeHackathon() { return nomeHackathon; }
  public void setNomeHackathon(String nomeHackathon) { this.nomeHackathon = nomeHackathon; }
  public Long getUtenteInvitatoId() { return utenteInvitatoId; }
  public void setUtenteInvitatoId(Long utenteInvitatoId) { this.utenteInvitatoId = utenteInvitatoId; }
  public String getUtenteInvitatoNome() { return utenteInvitatoNome; }
  public void setUtenteInvitatoNome(String utenteInvitatoNome) { this.utenteInvitatoNome = utenteInvitatoNome; }
  public Long getMittenteId() { return mittenteId; }
  public void setMittenteId(Long mittenteId) { this.mittenteId = mittenteId; }
  public InvitoStaff.StatoInvito getStato() { return stato; }
  public void setStato(InvitoStaff.StatoInvito stato) { this.stato = stato; }
  public LocalDateTime getDataInvito() { return dataInvito; }
  public void setDataInvito(LocalDateTime dataInvito) { this.dataInvito = dataInvito; }
}
