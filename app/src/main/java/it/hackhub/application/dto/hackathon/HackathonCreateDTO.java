package it.hackhub.application.dto.hackathon;

import java.time.LocalDateTime;

public class HackathonCreateDTO {

    private String nome;
    private String regolamento;
    private LocalDateTime inizioIscrizioni;
    private LocalDateTime scadenzaIscrizioni;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private LocalDateTime scadenzaSottomissioni;
    private String luogo;
    private Double premio;
    private Integer maxTeamSize;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getRegolamento() { return regolamento; }
    public void setRegolamento(String regolamento) { this.regolamento = regolamento; }
    public LocalDateTime getInizioIscrizioni() { return inizioIscrizioni; }
    public void setInizioIscrizioni(LocalDateTime inizioIscrizioni) { this.inizioIscrizioni = inizioIscrizioni; }
    public LocalDateTime getScadenzaIscrizioni() { return scadenzaIscrizioni; }
    public void setScadenzaIscrizioni(LocalDateTime scadenzaIscrizioni) { this.scadenzaIscrizioni = scadenzaIscrizioni; }
    public LocalDateTime getDataInizio() { return dataInizio; }
    public void setDataInizio(LocalDateTime dataInizio) { this.dataInizio = dataInizio; }
    public LocalDateTime getDataFine() { return dataFine; }
    public void setDataFine(LocalDateTime dataFine) { this.dataFine = dataFine; }
    public LocalDateTime getScadenzaSottomissioni() { return scadenzaSottomissioni; }
    public void setScadenzaSottomissioni(LocalDateTime scadenzaSottomissioni) { this.scadenzaSottomissioni = scadenzaSottomissioni; }
    public String getLuogo() { return luogo; }
    public void setLuogo(String luogo) { this.luogo = luogo; }
    public Double getPremio() { return premio; }
    public void setPremio(Double premio) { this.premio = premio; }
    public Integer getMaxTeamSize() { return maxTeamSize; }
    public void setMaxTeamSize(Integer maxTeamSize) { this.maxTeamSize = maxTeamSize; }
}
