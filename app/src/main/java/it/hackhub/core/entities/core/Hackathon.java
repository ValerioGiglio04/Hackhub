package it.hackhub.core.entities.core;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entità Hackathon
 */
@Entity
@Table(name = "Hackathons")
public class Hackathon {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String nome;
  private String regolamento;
  @Enumerated(EnumType.STRING)
  private StatoHackathon stato;
  @Column(name = "inizio_iscrizioni")
  private LocalDateTime inizioIscrizioni;
  @Column(name = "scadenza_iscrizioni")
  private LocalDateTime scadenzaIscrizioni;
  @Column(name = "data_inizio")
  private LocalDateTime dataInizio;
  @Column(name = "data_fine")
  private LocalDateTime dataFine;
  @Column(name = "scadenza_sottomissioni")
  private LocalDateTime scadenzaSottomissioni;
  private String luogo;
  private Double premio;
  @Column(name = "max_team_size")
  private Integer maxTeamSize;
  @OneToOne
  @JoinColumn(name = "id_team_vincitore")
  private Team teamVincitore;

  public Hackathon() {}

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

  public String getRegolamento() {
    return regolamento;
  }

  public void setRegolamento(String regolamento) {
    this.regolamento = regolamento;
  }

  public StatoHackathon getStato() {
    return stato;
  }

  public void setStato(StatoHackathon stato) {
    this.stato = stato;
  }

  public LocalDateTime getInizioIscrizioni() {
    return inizioIscrizioni;
  }

  public void setInizioIscrizioni(LocalDateTime inizioIscrizioni) {
    this.inizioIscrizioni = inizioIscrizioni;
  }

  public LocalDateTime getScadenzaIscrizioni() {
    return scadenzaIscrizioni;
  }

  public void setScadenzaIscrizioni(LocalDateTime scadenzaIscrizioni) {
    this.scadenzaIscrizioni = scadenzaIscrizioni;
  }

  public LocalDateTime getDataInizio() {
    return dataInizio;
  }

  public void setDataInizio(LocalDateTime dataInizio) {
    this.dataInizio = dataInizio;
  }

  public LocalDateTime getDataFine() {
    return dataFine;
  }

  public void setDataFine(LocalDateTime dataFine) {
    this.dataFine = dataFine;
  }

  public LocalDateTime getScadenzaSottomissioni() {
    return scadenzaSottomissioni;
  }

  public void setScadenzaSottomissioni(LocalDateTime scadenzaSottomissioni) {
    this.scadenzaSottomissioni = scadenzaSottomissioni;
  }

  public String getLuogo() {
    return luogo;
  }

  public void setLuogo(String luogo) {
    this.luogo = luogo;
  }

  public Double getPremio() {
    return premio;
  }

  public void setPremio(Double premio) {
    this.premio = premio;
  }

  public Integer getMaxTeamSize() {
    return maxTeamSize;
  }

  public void setMaxTeamSize(Integer maxTeamSize) {
    this.maxTeamSize = maxTeamSize;
  }

  public Team getTeamVincitore() {
    return teamVincitore;
  }

  public void setTeamVincitore(Team teamVincitore) {
    this.teamVincitore = teamVincitore;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Hackathon hackathon = (Hackathon) o;
    return Objects.equals(id, hackathon.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
