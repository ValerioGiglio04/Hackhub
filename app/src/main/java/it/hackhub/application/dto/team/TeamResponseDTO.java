package it.hackhub.application.dto.team;

import it.hackhub.application.dto.utente.UtenteSummaryDTO;
import java.util.List;

/**
 * DTO per la risposta con i dati di un Team (con capo e membri come summary).
 */
public class TeamResponseDTO {

  private Long id;
  private String nome;
  private UtenteSummaryDTO capo;
  private List<UtenteSummaryDTO> membri;
  private Integer numeroMembri;

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

  public UtenteSummaryDTO getCapo() {
    return capo;
  }

  public void setCapo(UtenteSummaryDTO capo) {
    this.capo = capo;
  }

  public List<UtenteSummaryDTO> getMembri() {
    return membri;
  }

  public void setMembri(List<UtenteSummaryDTO> membri) {
    this.membri = membri;
  }

  public Integer getNumeroMembri() {
    return numeroMembri;
  }

  public void setNumeroMembri(Integer numeroMembri) {
    this.numeroMembri = numeroMembri;
  }
}
