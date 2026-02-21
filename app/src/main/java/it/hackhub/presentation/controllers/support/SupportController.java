package it.hackhub.presentation.controllers.support;

import it.hackhub.application.dto.supporto.RichiestaSupportoCreateDTO;
import it.hackhub.application.dto.supporto.RichiestaSupportoResponseDTO;
import it.hackhub.application.dto.supporto.SegnalazioneViolazioneCreateDTO;
import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.application.exceptions.core.ValidationException;
import it.hackhub.application.handlers.support.SupportHandler;
import it.hackhub.application.mappers.SupportoDtoMapper;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.core.entities.support.RichiestaSupporto;
import java.util.List;

/**
 * Controller Support: Visualizza richieste supporto, Segnala violazioni, Crea richiesta supporto.
 */
public class SupportController {

  private static final int MAX_LUNGHEZZA_DESCRIZIONE = 2000;

  private final SupportHandler supportHandler;
  private final TeamRepository teamRepository;
  private final SupportoDtoMapper supportoDtoMapper;

  public SupportController(
    SupportHandler supportHandler,
    TeamRepository teamRepository,
    SupportoDtoMapper supportoDtoMapper
  ) {
    this.supportHandler = supportHandler;
    this.teamRepository = teamRepository;
    this.supportoDtoMapper = supportoDtoMapper;
  }

  /**
   * Crea una richiesta di supporto. Solo il capo del team può creare richieste per quel team.
   * @param dto dati della richiesta
   * @param utenteCorrenteId id dell'utente che effettua la richiesta (deve essere il capo del team)
   * @return DTO della richiesta creata (201 Created)
   * @throws UnauthorizedException se l'utente non è il capo del team (401)
   * @throws ValidationException se i dati non sono validi (400)
   */
  public RichiestaSupportoResponseDTO creaRichiestaSupporto(
    RichiestaSupportoCreateDTO dto,
    Long utenteCorrenteId
  ) {
    if (utenteCorrenteId == null) {
      throw new UnauthorizedException("Utente non autenticato");
    }
    if (dto.getTeamId() == null) {
      throw new ValidationException("teamId obbligatorio");
    }
    if (dto.getHackathonId() == null) {
      throw new ValidationException("hackathonId obbligatorio");
    }
    if (dto.getDescrizione() == null || dto.getDescrizione().isBlank()) {
      throw new ValidationException("descrizione obbligatoria");
    }
    if (dto.getDescrizione().length() > MAX_LUNGHEZZA_DESCRIZIONE) {
      throw new ValidationException("descrizione troppo lunga (max " + MAX_LUNGHEZZA_DESCRIZIONE + " caratteri)");
    }
    var teamOpt = teamRepository.findById(dto.getTeamId());
    if (teamOpt.isEmpty()) {
      throw new ValidationException("Team non trovato");
    }
    var team = teamOpt.get();
    if (team.getCapo() == null || !team.getCapo().getId().equals(utenteCorrenteId)) {
      throw new UnauthorizedException("Solo il capo del team può creare richieste di supporto per questo team");
    }
    RichiestaSupporto richiesta = supportoDtoMapper.toEntity(dto);
    RichiestaSupporto saved = supportHandler.creaRichiesta(richiesta);
    return supportoDtoMapper.toResponseDTO(saved);
  }

  public List<RichiestaSupportoResponseDTO> visualizzaRichiesteSupporto() {
    return supportHandler.ottieniRichiesteSupporto();
  }

  public void segnalaViolazione(SegnalazioneViolazioneCreateDTO dto) {
    supportHandler.segnalaViolazione(dto);
  }
}
