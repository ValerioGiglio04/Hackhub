package it.hackhub.application.handlers.core;

import it.hackhub.application.constants.PaymentConstants;
import it.hackhub.application.dto.hackathon.HackathonCreateDTO;
import it.hackhub.application.dto.hackathon.HackathonResponseDTO;
import it.hackhub.application.dto.hackathon.HackathonUpdateDTO;
import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.exceptions.submission.NotAllSubmissionsEvaluatedException;
import it.hackhub.application.repositories.associations.StaffHackatonRepository;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.SottomissioneRepository;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.application.repositories.core.ValutazioneRepository;
import it.hackhub.core.entities.associations.StaffHackaton;
import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.core.entities.core.Sottomissione;
import it.hackhub.core.entities.core.StatoHackathon;
import it.hackhub.core.entities.core.Team;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Handler per i casi d'uso Hackathon
 */
public class HackathonHandler {

  private final HackathonRepository hackathonRepository;
  private final TeamRepository teamRepository;
  private final SottomissioneRepository sottomissioneRepository;
  private final ValutazioneRepository valutazioneRepository;
  private final UtenteRepository utenteRepository;
  private final StaffHackatonRepository staffHackatonRepository;

  public HackathonHandler(HackathonRepository hackathonRepository) {
    this(hackathonRepository, null, null, null, null, null);
  }

  public HackathonHandler(
    HackathonRepository hackathonRepository,
    TeamRepository teamRepository,
    SottomissioneRepository sottomissioneRepository
  ) {
    this(hackathonRepository, teamRepository, sottomissioneRepository, null, null, null);
  }

  public HackathonHandler(
    HackathonRepository hackathonRepository,
    TeamRepository teamRepository,
    SottomissioneRepository sottomissioneRepository,
    ValutazioneRepository valutazioneRepository
  ) {
    this(hackathonRepository, teamRepository, sottomissioneRepository, valutazioneRepository, null, null);
  }

  public HackathonHandler(
    HackathonRepository hackathonRepository,
    TeamRepository teamRepository,
    SottomissioneRepository sottomissioneRepository,
    ValutazioneRepository valutazioneRepository,
    UtenteRepository utenteRepository,
    StaffHackatonRepository staffHackatonRepository
  ) {
    this.hackathonRepository = hackathonRepository;
    this.teamRepository = teamRepository;
    this.sottomissioneRepository = sottomissioneRepository;
    this.valutazioneRepository = valutazioneRepository;
    this.utenteRepository = utenteRepository;
    this.staffHackatonRepository = staffHackatonRepository;
  }

  /**
   * Assegna un utente allo staff dell'hackathon (dopo accettazione invito staff).
   */
  public void assegnaStaff(Long hackathonId, Long utenteId) {
    if (utenteRepository == null || staffHackatonRepository == null) {
      throw new IllegalStateException("UtenteRepository e StaffHackatonRepository richiesti per assegnaStaff");
    }
    Hackathon hackathon = hackathonRepository.findById(hackathonId)
        .orElseThrow(() -> new EntityNotFoundException("Hackathon", hackathonId));
    Utente utente = utenteRepository.findById(utenteId)
        .orElseThrow(() -> new EntityNotFoundException("Utente", utenteId));
    boolean alreadyAssigned = staffHackatonRepository.findByHackathonId(hackathonId).stream()
        .anyMatch(sh -> sh.getUtente() != null && utenteId.equals(sh.getUtente().getId()));
    if (alreadyAssigned) {
      throw new BusinessLogicException("L'utente è già assegnato a questo hackathon");
    }
    if (utente.getRuolo() == null || utente.getRuolo() == Utente.RuoloStaff.AUTENTICATO) {
      throw new BusinessLogicException("L'utente non ha un ruolo staff valido");
    }
    StaffHackaton sh = new StaffHackaton(hackathon, utente);
    staffHackatonRepository.save(sh);
  }

  /**
   * Crea hackathon: stato iniziale IN_ISCRIZIONE se now >= inizioIscrizioni, altrimenti IN_ATTESA.
   * Verifica che il premio non superi il limite PayPal Sandbox.
   */
  public HackathonResponseDTO creaHackathon(HackathonCreateDTO dto) {
    if (
      dto.getPremio() != null &&
      dto.getPremio() > PaymentConstants.MAX_PAYPAL_SANDBOX_AMOUNT
    ) {
      throw new BusinessLogicException(
        "Il premio supera il limite massimo consentito da PayPal Sandbox (" +
        PaymentConstants.MAX_PAYPAL_SANDBOX_AMOUNT +
        ")"
      );
    }
    LocalDateTime now = LocalDateTime.now();
    StatoHackathon statoIniziale = (
        dto.getInizioIscrizioni() != null &&
        !now.isBefore(dto.getInizioIscrizioni())
      )
      ? StatoHackathon.IN_ISCRIZIONE
      : StatoHackathon.IN_ATTESA;

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
    entity.setStato(statoIniziale);
    Hackathon saved = hackathonRepository.save(entity);
    HackathonResponseDTO response = new HackathonResponseDTO();
    response.setId(saved.getId());
    response.setNome(saved.getNome());
    response.setStato(saved.getStato());
    return response;
  }

  /** Avvio fase iscrizione (Tempo): IN_ATTESA → IN_ISCRIZIONE se now in [inizioIscrizioni, scadenzaIscrizioni). */
  public int avviaFaseIscrizione() {
    LocalDateTime now = LocalDateTime.now();
    List<Hackathon> list = hackathonRepository.findByStato(
      StatoHackathon.IN_ATTESA
    );
    int count = 0;
    for (Hackathon h : list) {
      if (
        h.getInizioIscrizioni() != null &&
        h.getScadenzaIscrizioni() != null &&
        !now.isBefore(h.getInizioIscrizioni()) &&
        now.isBefore(h.getScadenzaIscrizioni())
      ) {
        h.setStato(StatoHackathon.IN_ISCRIZIONE);
        hackathonRepository.save(h);
        count++;
      }
    }
    return count;
  }

  /** Conclusione fase iscrizione (Tempo): IN_ISCRIZIONE → IN_CORSO o ANNULLATO. */
  public int concludiFaseIscrizione() {
    LocalDateTime now = LocalDateTime.now();
    List<Hackathon> list = hackathonRepository.findByStato(
      StatoHackathon.IN_ISCRIZIONE
    );
    int count = 0;
    for (Hackathon h : list) {
      if (
        h.getScadenzaIscrizioni() != null &&
        !now.isBefore(h.getScadenzaIscrizioni())
      ) {
        int numTeams = teamRepository != null
          ? teamRepository.countTeamsIscritti(h.getId())
          : 0;
        h.setStato(
          numTeams >= 1 ? StatoHackathon.IN_CORSO : StatoHackathon.ANNULLATO
        );
        hackathonRepository.save(h);
        count++;
      }
    }
    return count;
  }

  /** Avvio fase svolgimento (Tempo): IN_CORSO → IN_SVOLGIMENTO se now in [dataInizio, dataFine). */
  public int avviaFaseSvolgimento() {
    LocalDateTime now = LocalDateTime.now();
    List<Hackathon> list = hackathonRepository.findByStato(
      StatoHackathon.IN_CORSO
    );
    int count = 0;
    for (Hackathon h : list) {
      if (
        h.getDataInizio() != null &&
        h.getDataFine() != null &&
        !now.isBefore(h.getDataInizio()) &&
        now.isBefore(h.getDataFine())
      ) {
        h.setStato(StatoHackathon.IN_SVOLGIMENTO);
        hackathonRepository.save(h);
        count++;
      }
    }
    return count;
  }

  /** Conclusione fase svolgimento (Tempo): IN_SVOLGIMENTO → CONCLUSIONE o ANNULLATO. */
  public int concludiFaseSvolgimento() {
    LocalDateTime now = LocalDateTime.now();
    List<Hackathon> list = hackathonRepository.findByStato(
      StatoHackathon.IN_SVOLGIMENTO
    );
    int count = 0;
    for (Hackathon h : list) {
      if (h.getDataFine() != null && !now.isBefore(h.getDataFine())) {
        List<Sottomissione> sottomissioni = sottomissioneRepository != null
          ? sottomissioneRepository.findByHackathonId(h.getId())
          : List.of();
        h.setStato(
          sottomissioni.isEmpty()
            ? StatoHackathon.ANNULLATO
            : StatoHackathon.CONCLUSO
        );
        hackathonRepository.save(h);
        count++;
      }
    }
    return count;
  }

  /**
   * Modifica Hackathon: aggiorna solo i campi non null del DTO.
   */
  public Hackathon aggiornaHackathon(Long hackathonId, HackathonUpdateDTO dto) {
    Hackathon h = hackathonRepository
      .findById(hackathonId)
      .orElseThrow(() -> new EntityNotFoundException("Hackathon", hackathonId));
    if (dto.getNome() != null) h.setNome(dto.getNome());
    if (dto.getRegolamento() != null) h.setRegolamento(dto.getRegolamento());
    if (dto.getStato() != null) h.setStato(dto.getStato());
    if (dto.getInizioIscrizioni() != null) h.setInizioIscrizioni(
      dto.getInizioIscrizioni()
    );
    if (dto.getScadenzaIscrizioni() != null) h.setScadenzaIscrizioni(
      dto.getScadenzaIscrizioni()
    );
    if (dto.getDataInizio() != null) h.setDataInizio(dto.getDataInizio());
    if (dto.getDataFine() != null) h.setDataFine(dto.getDataFine());
    if (dto.getScadenzaSottomissioni() != null) h.setScadenzaSottomissioni(
      dto.getScadenzaSottomissioni()
    );
    if (dto.getLuogo() != null) h.setLuogo(dto.getLuogo());
    if (dto.getPremio() != null) {
      if (dto.getPremio() > PaymentConstants.MAX_PAYPAL_SANDBOX_AMOUNT) {
        throw new BusinessLogicException(
          "Il premio supera il limite PayPal Sandbox"
        );
      }
      h.setPremio(dto.getPremio());
    }
    if (dto.getMaxTeamSize() != null) h.setMaxTeamSize(dto.getMaxTeamSize());
    return hackathonRepository.save(h);
  }

  /**
   * Proclama vincitore.
   */
  public void impostaVincitore(Long hackathonId, Long teamId) {
    Hackathon h = hackathonRepository
      .findById(hackathonId)
      .orElseThrow(() -> new EntityNotFoundException("Hackathon", hackathonId));
    Team team = teamRepository != null
      ? teamRepository
        .findById(teamId)
        .orElseThrow(() -> new EntityNotFoundException("Team", teamId))
      : null;
    if (team == null) throw new EntityNotFoundException("Team", teamId);
    if (
      teamRepository != null &&
      !teamRepository.isTeamIscritto(hackathonId, teamId)
    ) {
      throw new BusinessLogicException(
        "Il team non è iscritto a questo hackathon"
      );
    }
    if (h.getTeamVincitore() != null) {
      throw new BusinessLogicException("Questo hackathon ha già un vincitore");
    }
    if (sottomissioneRepository != null && valutazioneRepository != null) {
      List<Sottomissione> sottomissioni = sottomissioneRepository.findByHackathonId(
        hackathonId
      );
      boolean allEvaluated = sottomissioni
        .stream()
        .allMatch(s ->
          !valutazioneRepository.findBySottomissioneId(s.getId()).isEmpty()
        );
      if (!allEvaluated) {
        throw new NotAllSubmissionsEvaluatedException(hackathonId);
      }
    }
    h.setTeamVincitore(team);
    h.setStato(StatoHackathon.CONCLUSO);
    hackathonRepository.save(h);
  }
}
