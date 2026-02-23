package it.hackhub.core.entities.core;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entità Valutazione
 */
@Entity
@Table(name = "Valutazioni")
public class Valutazione {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "id_sottomissione", nullable = false)
  private Long sottomissioneId;
  @Column(name = "id_giudice", nullable = false)
  private Long giudiceId;
  private Integer punteggio;
  private String commento;
  @Column(name = "data_valutazione")
  private LocalDateTime dataValutazione;

  public Valutazione() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getSottomissioneId() {
    return sottomissioneId;
  }

  public void setSottomissioneId(Long sottomissioneId) {
    this.sottomissioneId = sottomissioneId;
  }

  public Long getGiudiceId() {
    return giudiceId;
  }

  public void setGiudiceId(Long giudiceId) {
    this.giudiceId = giudiceId;
  }

  public Integer getPunteggio() {
    return punteggio;
  }

  public void setPunteggio(Integer punteggio) {
    this.punteggio = punteggio;
  }

  public String getCommento() {
    return commento;
  }

  public void setCommento(String commento) {
    this.commento = commento;
  }

  public LocalDateTime getDataValutazione() {
    return dataValutazione;
  }

  public void setDataValutazione(LocalDateTime dataValutazione) {
    this.dataValutazione = dataValutazione;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Valutazione that = (Valutazione) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
