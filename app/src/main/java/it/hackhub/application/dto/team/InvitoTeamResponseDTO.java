package it.hackhub.application.dto.team;

import it.hackhub.core.entities.associations.InvitoTeam;
import java.time.LocalDateTime;

/**
 * DTO di risposta per un invito a unirsi a un team.
 */
public class InvitoTeamResponseDTO {

  private Long id;
  private Long teamId;
  private String nomeTeam;
  private Long utenteInvitatoId;
  private String utenteInvitatoNome;
  private Long mittenteCapoId;
  private InvitoTeam.StatoInvito stato;
  private LocalDateTime dataInvito;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getTeamId() { return teamId; }
  public void setTeamId(Long teamId) { this.teamId = teamId; }
  public String getNomeTeam() { return nomeTeam; }
  public void setNomeTeam(String nomeTeam) { this.nomeTeam = nomeTeam; }
  public Long getUtenteInvitatoId() { return utenteInvitatoId; }
  public void setUtenteInvitatoId(Long utenteInvitatoId) { this.utenteInvitatoId = utenteInvitatoId; }
  public String getUtenteInvitatoNome() { return utenteInvitatoNome; }
  public void setUtenteInvitatoNome(String utenteInvitatoNome) { this.utenteInvitatoNome = utenteInvitatoNome; }
  public Long getMittenteCapoId() { return mittenteCapoId; }
  public void setMittenteCapoId(Long mittenteCapoId) { this.mittenteCapoId = mittenteCapoId; }
  public InvitoTeam.StatoInvito getStato() { return stato; }
  public void setStato(InvitoTeam.StatoInvito stato) { this.stato = stato; }
  public LocalDateTime getDataInvito() { return dataInvito; }
  public void setDataInvito(LocalDateTime dataInvito) { this.dataInvito = dataInvito; }
}
