package it.hackhub.core.builders.core;

import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.StatoHackathon;
import java.time.LocalDateTime;

/**
 * Builder per la classe Hackathon (senza staff; lo staff si gestisce via StaffHackaton).
 */
public class HackathonBuilder {

  private String nome;
  private String regolamento;
  private StatoHackathon stato = StatoHackathon.IN_ATTESA;
  private LocalDateTime inizioIscrizioni;
  private LocalDateTime scadenzaIscrizioni;
  private LocalDateTime dataInizio;
  private LocalDateTime dataFine;
  private LocalDateTime scadenzaSottomissioni;
  private String luogo;
  private Double premio;
  private Integer maxTeamSize;

  public HackathonBuilder nome(String nome) {
    this.nome = nome;
    return this;
  }

  public HackathonBuilder regolamento(String regolamento) {
    this.regolamento = regolamento;
    return this;
  }

  public HackathonBuilder stato(StatoHackathon stato) {
    this.stato = stato;
    return this;
  }

  public HackathonBuilder inizioIscrizioni(LocalDateTime inizioIscrizioni) {
    this.inizioIscrizioni = inizioIscrizioni;
    return this;
  }

  public HackathonBuilder scadenzaIscrizioni(LocalDateTime scadenzaIscrizioni) {
    this.scadenzaIscrizioni = scadenzaIscrizioni;
    return this;
  }

  public HackathonBuilder dataInizio(LocalDateTime dataInizio) {
    this.dataInizio = dataInizio;
    return this;
  }

  public HackathonBuilder dataFine(LocalDateTime dataFine) {
    this.dataFine = dataFine;
    return this;
  }

  public HackathonBuilder scadenzaSottomissioni(LocalDateTime scadenzaSottomissioni) {
    this.scadenzaSottomissioni = scadenzaSottomissioni;
    return this;
  }

  public HackathonBuilder luogo(String luogo) {
    this.luogo = luogo;
    return this;
  }

  public HackathonBuilder premio(Double premio) {
    this.premio = premio;
    return this;
  }

  public HackathonBuilder maxTeamSize(Integer maxTeamSize) {
    this.maxTeamSize = maxTeamSize;
    return this;
  }

  public Hackathon build() {
    if (nome == null || nome.trim().isEmpty()) {
      throw new IllegalArgumentException("Il nome dell'hackathon è obbligatorio");
    }
    Hackathon h = new Hackathon();
    h.setNome(nome);
    h.setRegolamento(regolamento);
    h.setStato(stato);
    h.setInizioIscrizioni(inizioIscrizioni);
    h.setScadenzaIscrizioni(scadenzaIscrizioni);
    h.setDataInizio(dataInizio);
    h.setDataFine(dataFine);
    h.setScadenzaSottomissioni(scadenzaSottomissioni);
    h.setLuogo(luogo);
    h.setPremio(premio);
    h.setMaxTeamSize(maxTeamSize);
    return h;
  }
}
