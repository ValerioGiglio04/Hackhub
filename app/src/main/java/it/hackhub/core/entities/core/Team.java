package it.hackhub.core.entities.core;

import it.hackhub.core.entities.associations.MembroTeam;
import it.hackhub.core.entities.roles.TeamMember;
import it.hackhub.core.entities.roles.TeamLeader;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entità Team (iscrizioni, vincitore).
 */
@Entity
@Table(name = "Teams")
public class Team {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String nome;
  @Column(name = "email_paypal")
  private String emailPaypal;
  @ManyToOne
  @JoinColumn(name = "id_capo")
  private Utente capo;
  @ManyToMany
  @JoinTable(
    name = "Team_Membri",
    joinColumns = @JoinColumn(name = "id_team"),
    inverseJoinColumns = @JoinColumn(name = "id_utente")
  )
  private List<Utente> membri = new ArrayList<>();

  public Team() {
    if (this.membri == null) this.membri = new ArrayList<>();
  }

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

  public Utente getCapo() {
    return capo;
  }

  public void setCapo(Utente capo) {
    this.capo = capo;
  }

  public List<Utente> getMembri() {
    return membri;
  }

  public void setMembri(List<Utente> membri) {
    this.membri = membri;
  }

  public MembroTeam getMembroTeam(Utente utente) {
    if (utente == null) {
      return null;
    }

    if (capo != null && capo.getId().equals(utente.getId())) {
      return new TeamLeaderImpl(utente, this);
    }

    if (membri != null && membri.stream().anyMatch(m -> m.getId().equals(utente.getId()))) {
      return new TeamMemberImpl(utente, this);
    }

    return null;
  }

  public boolean contieneUtente(Utente utente) {
    if (utente == null) {
      return false;
    }

    boolean isCapo = capo != null && capo.getId().equals(utente.getId());
    boolean isMembro = membri != null && membri.stream().anyMatch(m -> m.getId().equals(utente.getId()));

    return isCapo || isMembro;
  }

  public boolean isCapo(Utente utente) {
    return utente != null && capo != null && capo.getId().equals(utente.getId());
  }

  private static class TeamMemberImpl implements TeamMember {
    private final Utente utente;
    private final Team team;
    
    public TeamMemberImpl(Utente utente, Team team) {
      this.utente = utente;
      this.team = team;
    }
    
    @Override
    public Utente getUtente() { 
      return utente; 
    }
    
    @Override
    public Team getTeam() { 
      return team; 
    }
    
    @Override
    public boolean isCapoTeam() { 
      return team.isCapo(utente); 
    }
  }

  private static class TeamLeaderImpl implements TeamLeader {
    private final Utente utente;
    private final Team team;
    
    public TeamLeaderImpl(Utente utente, Team team) {
      this.utente = utente;
      this.team = team;
    }
    
    @Override
    public Utente getUtente() { 
      return utente; 
    }
    
    @Override
    public Team getTeam() { 
      return team; 
    }
    
    @Override
    public boolean isCapoTeam() { 
      return true; 
    }
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
