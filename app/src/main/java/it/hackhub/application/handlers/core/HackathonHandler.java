package it.hackhub.application.handlers.core;

import it.hackhub.application.constants.PaymentConstants;
import it.hackhub.application.dto.hackathon.HackathonCreateDTO;
import it.hackhub.application.dto.hackathon.HackathonResponseDTO;
import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.SottomissioneRepository;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.Sottomissione;
import it.hackhub.core.entities.core.StatoHackathon;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Handler per i casi d'uso Hackathon
 */
public class HackathonHandler {

  private final HackathonRepository hackathonRepository;
  private final TeamRepository teamRepository;
  private final SottomissioneRepository sottomissioneRepository;

  public HackathonHandler(HackathonRepository hackathonRepository) {
    this(hackathonRepository, null, null);
  }

  public HackathonHandler(
    HackathonRepository hackathonRepository,
    TeamRepository teamRepository,
    SottomissioneRepository sottomissioneRepository
  ) {
    this.hackathonRepository = hackathonRepository;
    this.teamRepository = teamRepository;
    this.sottomissioneRepository = sottomissioneRepository;
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
}
