package it.hackhub.presentation.controllers.core;

import it.hackhub.application.dto.hackathon.HackathonCreateDTO;
import it.hackhub.application.dto.hackathon.HackathonResponseDTO;
import it.hackhub.application.dto.hackathon.HackathonStaffAssignmentDTO;
import it.hackhub.application.dto.hackathon.HackathonUpdateDTO;
import it.hackhub.application.dto.hackathon.HackathonWinnerDTO;
import it.hackhub.application.dto.hackathon.InvitoStaffResponseDTO;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.handlers.InvitiStaffHandler;
import it.hackhub.application.handlers.core.HackathonHandler;
import it.hackhub.application.mappers.HackathonDtoMapper;
import it.hackhub.application.mappers.InvitoStaffDtoMapper;
import it.hackhub.core.entities.core.Hackathon;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller Hackathon
 */
public class HackathonController {

  private final HackathonHandler hackathonHandler;
  private final InvitiStaffHandler invitiStaffHandler;

  public HackathonController(HackathonHandler hackathonHandler) {
    this(hackathonHandler, null);
  }

  public HackathonController(HackathonHandler hackathonHandler, InvitiStaffHandler invitiStaffHandler) {
    this.hackathonHandler = hackathonHandler;
    this.invitiStaffHandler = invitiStaffHandler;
  }

  /**
   * GET /api/hackathon/pubblico – Elenco pubblico hackathon (use case: Visualizza informazioni hackathon).
   */
  public List<HackathonResponseDTO> ottieniElencoPubblico() {
    return hackathonHandler.ottieniTuttiGliHackathon().stream()
        .map(HackathonDtoMapper::toResponseDTO)
        .collect(Collectors.toList());
  }

  /**
   * GET /api/hackathon/{hackathonId} – Dettaglio hackathon per id. 404 se non trovato.
   */
  public HackathonResponseDTO ottieniHackathonPerId(Long hackathonId) {
    return hackathonHandler.ottieniHackathonPerId(hackathonId)
        .map(HackathonDtoMapper::toResponseDTO)
        .orElseThrow(() -> new EntityNotFoundException("Hackathon", hackathonId));
  }

  public HackathonResponseDTO creaHackathon(HackathonCreateDTO dto) {
    return hackathonHandler.creaHackathon(dto);
  }

  public HackathonResponseDTO aggiornaHackathon(
    Long hackathonId,
    HackathonUpdateDTO dto
  ) {
    Hackathon h = hackathonHandler.aggiornaHackathon(hackathonId, dto);
    HackathonResponseDTO res = new HackathonResponseDTO();
    res.setId(h.getId());
    res.setNome(h.getNome());
    res.setStato(h.getStato());
    return res;
  }

  public void impostaVincitore(HackathonWinnerDTO dto) {
    hackathonHandler.impostaVincitore(dto.getHackathonId(), dto.getTeamId());
  }

  /** Avvio fase iscrizione (chiamato da HackathonScheduler). */
  public int avviaFaseIscrizione() {
    return hackathonHandler.avviaFaseIscrizione();
  }

  /** Conclusione fase iscrizione (chiamato da HackathonScheduler). */
  public int concludiFaseIscrizione() {
    return hackathonHandler.concludiFaseIscrizione();
  }

  /** Avvio fase svolgimento (chiamato da HackathonScheduler). */
  public int avviaFaseSvolgimento() {
    return hackathonHandler.avviaFaseSvolgimento();
  }

  /** Conclusione fase svolgimento (chiamato da HackathonScheduler). */
  public int concludiFaseSvolgimento() {
    return hackathonHandler.concludiFaseSvolgimento();
  }

  /**
   * Invita staff a un hackathon (Use case: Invita staff – Organizzatore).
   * POST /api/hackathon/invita-staff
   */
  public InvitoStaffResponseDTO invitaStaff(HackathonStaffAssignmentDTO dto, Long utenteCorrenteId) {
    if (invitiStaffHandler == null) {
      throw new IllegalStateException("InvitiStaffHandler non configurato");
    }
    var invito = invitiStaffHandler.invitaStaff(
        dto.getHackathonId(),
        dto.getUtenteId(),
        utenteCorrenteId
    );
    return InvitoStaffDtoMapper.toResponseDTO(invito);
  }
}
