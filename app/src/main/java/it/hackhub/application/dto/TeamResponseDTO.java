package it.hackhub.application.dto;

import java.util.List;

/**
 * DTO per la risposta con i dati di un Team.
 */
public class TeamResponseDTO {
    private Long id;
    private String nome;
    private String emailPaypal;
    private UtenteDTO capo;
    private List<UtenteDTO> membri;

    public TeamResponseDTO() {}

    public TeamResponseDTO(Long id, String nome, String emailPaypal, UtenteDTO capo, List<UtenteDTO> membri) {
        this.id = id;
        this.nome = nome;
        this.emailPaypal = emailPaypal;
        this.capo = capo;
        this.membri = membri;
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

    public UtenteDTO getCapo() {
        return capo;
    }

    public void setCapo(UtenteDTO capo) {
        this.capo = capo;
    }

    public List<UtenteDTO> getMembri() {
        return membri;
    }

    public void setMembri(List<UtenteDTO> membri) {
        this.membri = membri;
    }
}
