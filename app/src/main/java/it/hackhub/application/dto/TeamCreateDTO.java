package it.hackhub.application.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO per la creazione di un team (use case Crea Team).
 */
public class TeamCreateDTO {

  private String nome;
  private Long capoId;
  private List<Long> utentiDaInvitareIds;

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public Long getCapoId() {
    return capoId;
  }

  public void setCapoId(Long capoId) {
    this.capoId = capoId;
  }

  public List<Long> getUtentiDaInvitareIds() {
    return utentiDaInvitareIds != null ? utentiDaInvitareIds : new ArrayList<>();
  }

  public void setUtentiDaInvitareIds(List<Long> utentiDaInvitareIds) {
    this.utentiDaInvitareIds = utentiDaInvitareIds;
  }
}
