package it.hackhub.core.builders.core;

import it.hackhub.core.entities.core.Team;
import it.hackhub.core.entities.core.Utente;
import java.util.ArrayList;
import java.util.List;

public class TeamBuilder {

  private String nome;
  private Utente capo;
  private List<Utente> membri = new ArrayList<>();
  private String emailPaypal;

  public TeamBuilder nome(String nome) {
    this.nome = nome;
    return this;
  }

  public TeamBuilder capo(Utente capo) {
    this.capo = capo;
    return this;
  }

  public TeamBuilder aggiungiMembro(Utente membro) {
    if (membro == null) return this;
    if (capo != null && membro.getId() != null && capo.getId() != null && membro.getId().equals(capo.getId())) {
      return this;
    }
    if (membri.stream().noneMatch(m -> m.getId() != null && membro.getId() != null && m.getId().equals(membro.getId()))) {
      membri.add(membro);
    }
    return this;
  }

  public TeamBuilder membri(List<Utente> membri) {
    this.membri = membri != null ? new ArrayList<>(membri) : new ArrayList<>();
    return this;
  }

  public TeamBuilder emailPaypal(String emailPaypal) {
    this.emailPaypal = emailPaypal;
    return this;
  }

  public Team build() {
    if (nome == null || nome.trim().isEmpty()) {
      throw new IllegalArgumentException("Il nome del team è obbligatorio");
    }
    if (capo == null) {
      throw new IllegalArgumentException("Il capo del team è obbligatorio");
    }
    Team team = new Team();
    team.setNome(nome);
    team.setCapo(capo);
    team.setMembri(membri);
    team.setEmailPaypal(emailPaypal);
    return team;
  }
}
