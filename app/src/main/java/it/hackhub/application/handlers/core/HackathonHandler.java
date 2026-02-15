package it.hackhub.application.handlers.core;

import it.hackhub.application.dto.hackathon.HackathonCreateDTO;
import it.hackhub.application.dto.hackathon.HackathonResponseDTO;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.StatoHackathon;

/**
 * Handler per i casi d'uso Hackathon – iterazione 1: stub creaHackathon (wiring verificabile).
 */
public class HackathonHandler {

    private final HackathonRepository hackathonRepository;

    public HackathonHandler(HackathonRepository hackathonRepository) {
        this.hackathonRepository = hackathonRepository;
    }

    /**
     * Stub: crea entità da DTO, salva, ritorna response minimale (id, nome, stato).
     */
    public HackathonResponseDTO creaHackathon(HackathonCreateDTO dto) {
        Hackathon entity = new Hackathon();
        entity.setNome(dto.getNome());
        entity.setRegolamento(dto.getRegolamento());
        entity.setInizioIscrizioni(dto.getInizioIscrizioni());
        entity.setScadenzaIscrizioni(dto.getScadenzaIscrizioni());
        entity.setDataInizio(dto.getDataInizio());
        entity.setDataFine(dto.getDataFine());
        entity.setScadenzaSottomissioni(dto.getScadenzaSottomissioni());
        entity.setLuogo(dto.getLuogo());
        entity.setPremio(dto.getPremio());
        entity.setMaxTeamSize(dto.getMaxTeamSize());
        entity.setStato(StatoHackathon.IN_ATTESA);
        Hackathon saved = hackathonRepository.save(entity);
        HackathonResponseDTO response = new HackathonResponseDTO();
        response.setId(saved.getId());
        response.setNome(saved.getNome());
        response.setStato(saved.getStato());
        return response;
    }
}
