package it.hackhub.application.dto.hackathon;

import it.hackhub.core.entities.core.StatoHackathon;

public class HackathonResponseDTO {

    private Long id;
    private String nome;
    private StatoHackathon stato;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public StatoHackathon getStato() { return stato; }
    public void setStato(StatoHackathon stato) { this.stato = stato; }
}
