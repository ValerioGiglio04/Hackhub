package it.hackhub.core.entities.core;

import java.util.Objects;

/**
 * Entità Team (iscrizioni, vincitore).
 */
public class Team {

  private Long id;
  private String nome;
  private String emailPaypal;

  public Team() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getEmailPaypal() {
    return emailPaypal;
  }

  public void setEmailPaypal(String emailPaypal) {
    this.emailPaypal = emailPaypal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Team team = (Team) o;
    return Objects.equals(id, team.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
