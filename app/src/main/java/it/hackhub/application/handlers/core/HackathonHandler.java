package it.hackhub.application.handlers.core;

import it.hackhub.application.constants.PaymentConstants;
import it.hackhub.application.dto.hackathon.HackathonCreateDTO;
import it.hackhub.application.dto.hackathon.HackathonResponseDTO;
import it.hackhub.application.dto.hackathon.HackathonUpdateDTO;
import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.exceptions.submission.NotAllSubmissionsEvaluatedException;
import it.hackhub.application.repositories.associations.IscrizioneTeamHackathonRepository;
import it.hackhub.application.repositories.associations.StaffHackatonRepository;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.SottomissioneRepository;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.application.repositories.core.ValutazioneRepository;
import it.hackhub.infrastructure.external.pagamenti.PaymentService;
import it.hackhub.core.entities.associations.StaffHackaton;
import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.core.entities.core.Sottomissione;
import it.hackhub.core.entities.core.StatoHackathon;
import it.hackhub.core.entities.core.Team;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handler per i casi d'uso Hackathon
 */
@Service
public class HackathonHandler {

  private static final Logger log = LoggerFactory.getLogger(
    HackathonHandler.class
  );

  private final HackathonRepository hackathonRepository;
  private final TeamRepository teamRepository;
  private final SottomissioneRepository sottomissioneRepository;
  private final ValutazioneRepository valutazioneRepository;
  private final UtenteRepository utenteRepository;
  private final StaffHackatonRepository staffHackatonRepository;
  private final IscrizioneTeamHackathonRepository iscrizioneTeamHackathonRepository;
  private final PaymentService paymentService;

  @Autowired
  public HackathonHandler(
    HackathonRepository hackathonRepository,
    TeamRepository teamRepository,
    SottomissioneRepository sottomissioneRepository,
    ValutazioneRepository valutazioneRepository,
    UtenteRepository utenteRepository,
    StaffHackatonRepository staffHackatonRepository,
    IscrizioneTeamHackathonRepository iscrizioneTeamHackathonRepository,
    PaymentService paymentService
  ) {
    this.hackathonRepository = hackathonRepository;
    this.teamRepository = teamRepository;
    this.sottomissioneRepository = sottomissioneRepository;
    this.valutazioneRepository = valutazioneRepository;
    this.utenteRepository = utenteRepository;
    this.staffHackatonRepository = staffHackatonRepository;
    this.iscrizioneTeamHackathonRepository = iscrizioneTeamHackathonRepository;
    this.paymentService = paymentService;
  }

  /**
   * Restituisce tutti gli hackathon (use case: Visualizza informazioni hackathon – elenco pubblico).
   */
  public List<Hackathon> ottieniTuttiGliHackathon() {
    return hackathonRepository.findAll();
  }

  /**
   * Restituisce un hackathon per id (use case: Visualizza informazioni hackathon – dettaglio).
   * Ritorna Optional vuoto se non trovato (il controller gestirà 404).
   */
  public Optional<Hackathon> ottieniHackathonPerId(Long hackathonId) {
    return hackathonRepository.findById(hackathonId);
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
        int numTeams = iscrizioneTeamHackathonRepository != null
          ? (int) iscrizioneTeamHackathonRepository.countByHackathonId(h.getId())
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
   * Proclama vincitore. Se il team ha email PayPal e l'hackathon ha premio > 0,
   * effettua il payout PayPal prima di impostare il vincitore.
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
      iscrizioneTeamHackathonRepository != null &&
      iscrizioneTeamHackathonRepository.findByTeamIdAndHackathonId(teamId, hackathonId).isEmpty()
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

    boolean canPay =
      team.getEmailPaypal() != null &&
      !team.getEmailPaypal().isEmpty() &&
      h.getPremio() != null &&
      h.getPremio() > 0;

    if (!canPay) {
      if (team.getEmailPaypal() == null || team.getEmailPaypal().isEmpty()) {
        log.info(
          "impostaVincitore: pagamento PayPal non effettuato - team id={} senza email PayPal impostata",
          teamId
        );
      }
      if (h.getPremio() == null || h.getPremio() <= 0) {
        log.info(
          "impostaVincitore: pagamento PayPal non effettuato - hackathon id={} senza premio (premio={})",
          hackathonId,
          h.getPremio()
        );
      }
      h.setTeamVincitore(team);
      h.setStato(StatoHackathon.CONCLUSO);
      hackathonRepository.save(h);
      return;
    }

    if (h.getPremio() > PaymentConstants.MAX_PAYPAL_SANDBOX_AMOUNT) {
      throw new BusinessLogicException(
        String.format(
          "Impossibile processare il pagamento: il premio di %.2f€ supera il limite massimo di %.2f€ consentito da PayPal Sandbox",
          h.getPremio(),
          PaymentConstants.MAX_PAYPAL_SANDBOX_AMOUNT
        )
      );
    }

    try {
      Double amount = h.getPremio();
      String referenceId = "HACK-" + hackathonId + "-TEAM-" + teamId;
      log.info(
        "impostaVincitore: chiamata API PayPal per payout - hackathonId={}, teamId={}, email={}, amount={} EUR",
        hackathonId,
        teamId,
        team.getEmailPaypal(),
        amount
      );

      PaymentService.PaymentResponse response = paymentService.processPayment(
        team.getEmailPaypal(),
        amount,
        "EUR",
        referenceId
      );

      if (!response.success()) {
        log.error("PayPal payout fallito: {}", response.errorMessage());
        throw new BusinessLogicException(
          "Pagamento fallito: " + response.errorMessage()
        );
      }
      log.info(
        "PayPal payout completato: payoutBatchId={}, batchStatus={}, senderBatchId={}, detailsUrl={}, referenceId={}, teamId={}, hackathonId={}",
        response.transactionId(),
        response.batchStatus(),
        response.senderBatchId(),
        response.payoutDetailsUrl(),
        referenceId,
        teamId,
        hackathonId
      );
    } catch (BusinessLogicException e) {
      throw e;
    } catch (Exception e) {
      log.error("Errore durante il pagamento PayPal: {}", e.getMessage(), e);
      throw new BusinessLogicException(
        "Pagamento fallito: " + e.getMessage()
      );
    }

    h.setTeamVincitore(team);
    h.setStato(StatoHackathon.CONCLUSO);
    hackathonRepository.save(h);
  }
}
