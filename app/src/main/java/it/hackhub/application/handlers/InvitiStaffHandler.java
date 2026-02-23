package it.hackhub.application.handlers;

import it.hackhub.application.exceptions.core.BusinessLogicException;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.handlers.core.HackathonHandler;
import it.hackhub.application.repositories.associations.InvitoStaffRepository;
import it.hackhub.application.repositories.associations.StaffHackatonRepository;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.associations.InvitoStaff;
import it.hackhub.core.entities.associations.StaffHackaton;
import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.StatoHackathon;
import it.hackhub.core.entities.core.Utente;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handler per inviti staff hackathon (invita, accetta, rifiuta).
 */
@Service
public class InvitiStaffHandler {

  private static final int MAX_ORGANIZZATORI = 3;
  private static final int MAX_MENTORI = 5;
  private static final int MAX_GIUDICI = 5;

  private final InvitoStaffRepository invitoStaffRepository;
  private final HackathonHandler hackathonHandler;
  private final HackathonRepository hackathonRepository;
  private final UtenteRepository utenteRepository;
  private final StaffHackatonRepository staffHackatonRepository;

  @Autowired
  public InvitiStaffHandler(
    InvitoStaffRepository invitoStaffRepository,
    HackathonHandler hackathonHandler,
    HackathonRepository hackathonRepository,
    UtenteRepository utenteRepository,
    StaffHackatonRepository staffHackatonRepository
  ) {
    this.invitoStaffRepository = invitoStaffRepository;
    this.hackathonHandler = hackathonHandler;
    this.hackathonRepository = hackathonRepository;
    this.utenteRepository = utenteRepository;
    this.staffHackatonRepository = staffHackatonRepository;
  }

  /**
   * Crea un invito per un utente a far parte dello staff di un hackathon. Solo un organizzatore può invitare.
   */
  public InvitoStaff invitaStaff(
    Long hackathonId,
    Long utenteInvitatoId,
    Long mittenteId
  ) {
    if (
      hackathonRepository == null ||
      utenteRepository == null ||
      staffHackatonRepository == null
    ) {
      throw new IllegalStateException(
        "HackathonRepository, UtenteRepository e StaffHackatonRepository richiesti per invitaStaff"
      );
    }
    Hackathon hackathon = hackathonRepository
      .findById(hackathonId)
      .orElseThrow(() -> new EntityNotFoundException("Hackathon", hackathonId));
    Utente staff = utenteRepository
      .findById(utenteInvitatoId)
      .orElseThrow(() -> new EntityNotFoundException("Utente", utenteInvitatoId)
      );
    Utente mittente = utenteRepository
      .findById(mittenteId)
      .orElseThrow(() -> new EntityNotFoundException("Utente", mittenteId));

    if (mittente.getRuolo() != Utente.RuoloStaff.ORGANIZZATORE) {
      throw new BusinessLogicException(
        "Solo un organizzatore può invitare staff a un hackathon."
      );
    }
    if (staff.getRuolo() == Utente.RuoloStaff.AUTENTICATO) {
      throw new BusinessLogicException(
        "L'utente è un AUTENTICATO. Solo utenti con ruolo staff (ORGANIZZATORE, MENTORE, GIUDICE) possono essere invitati."
      );
    }
    List<StaffHackaton> staffAssegnati = staffHackatonRepository.findByHackathonId(
      hackathonId
    );
    boolean giaAssegnato = staffAssegnati
      .stream()
      .anyMatch(sh ->
        sh.getUtente() != null &&
        utenteInvitatoId.equals(sh.getUtente().getId())
      );
    if (giaAssegnato) {
      throw new BusinessLogicException(
        "L'utente è già assegnato a questo hackathon."
      );
    }
    Optional<InvitoStaff> invitoEsistente = invitoStaffRepository.findByHackathonIdAndUtenteInvitatoIdAndStato(
      hackathonId,
      utenteInvitatoId,
      InvitoStaff.StatoInvito.PENDING
    );
    if (invitoEsistente.isPresent()) {
      throw new BusinessLogicException(
        "Esiste già un invito pendente per questo utente a questo hackathon."
      );
    }
    if (hackathon.getStato() == StatoHackathon.CONCLUSO) {
      throw new BusinessLogicException(
        "L'hackathon è già concluso. Non è possibile invitare staff."
      );
    }
    long numOrg = staffAssegnati
      .stream()
      .filter(sh ->
        sh.getUtente() != null &&
        sh.getUtente().getRuolo() == Utente.RuoloStaff.ORGANIZZATORE
      )
      .count();
    long numMentori = staffAssegnati
      .stream()
      .filter(sh ->
        sh.getUtente() != null &&
        sh.getUtente().getRuolo() == Utente.RuoloStaff.MENTORE
      )
      .count();
    long numGiudici = staffAssegnati
      .stream()
      .filter(sh ->
        sh.getUtente() != null &&
        sh.getUtente().getRuolo() == Utente.RuoloStaff.GIUDICE
      )
      .count();
    if (
      staff.getRuolo() == Utente.RuoloStaff.ORGANIZZATORE &&
      numOrg >= MAX_ORGANIZZATORI
    ) {
      throw new BusinessLogicException(
        "L'hackathon ha già raggiunto il numero massimo di " +
        MAX_ORGANIZZATORI +
        " organizzatori."
      );
    }
    if (
      staff.getRuolo() == Utente.RuoloStaff.MENTORE && numMentori >= MAX_MENTORI
    ) {
      throw new BusinessLogicException(
        "L'hackathon ha già raggiunto il numero massimo di " +
        MAX_MENTORI +
        " mentori."
      );
    }
    if (
      staff.getRuolo() == Utente.RuoloStaff.GIUDICE && numGiudici >= MAX_GIUDICI
    ) {
      throw new BusinessLogicException(
        "L'hackathon ha già raggiunto il numero massimo di " +
        MAX_GIUDICI +
        " giudici."
      );
    }

    InvitoStaff invito = new InvitoStaff();
    invito.setHackathon(hackathon);
    invito.setUtenteInvitato(staff);
    invito.setMittente(mittente);
    invito.setStato(InvitoStaff.StatoInvito.PENDING);
    invito.setDataInvito(LocalDateTime.now());
    return invitoStaffRepository.save(invito);
  }

  public void accettaInvitoStaff(Long invitoId, Long utenteCorrenteId) {
    InvitoStaff invito = invitoStaffRepository
      .findByIdWithDetails(invitoId)
      .orElseThrow(() -> new EntityNotFoundException("Invito staff", invitoId));
    if (invito.getStato() != InvitoStaff.StatoInvito.PENDING) {
      throw new BusinessLogicException("Questo invito non è più valido");
    }
    if (!invito.getUtenteInvitato().getId().equals(utenteCorrenteId)) {
      throw new BusinessLogicException(
        "Puoi accettare solo gli inviti rivolti a te"
      );
    }
    hackathonHandler.assegnaStaff(
      invito.getHackathon().getId(),
      invito.getUtenteInvitato().getId()
    );
    invito.setStato(InvitoStaff.StatoInvito.ACCETTATO);
    invitoStaffRepository.save(invito);
  }

  public InvitoStaff rifiutaInvitoStaff(Long invitoId, Long utenteCorrenteId) {
    InvitoStaff invito = invitoStaffRepository
      .findByIdWithDetails(invitoId)
      .orElseThrow(() -> new EntityNotFoundException("Invito staff", invitoId));
    if (invito.getStato() != InvitoStaff.StatoInvito.PENDING) {
      throw new BusinessLogicException("Questo invito non è più valido");
    }
    if (!invito.getUtenteInvitato().getId().equals(utenteCorrenteId)) {
      throw new BusinessLogicException(
        "Puoi rifiutare solo gli inviti rivolti a te"
      );
    }
    invito.setStato(InvitoStaff.StatoInvito.RIFIUTATO);
    return invitoStaffRepository.save(invito);
  }

  public List<InvitoStaff> ottieniInvitiRicevutiPending(Long utenteId) {
    return invitoStaffRepository.findByUtenteInvitatoIdAndStato(
      utenteId,
      InvitoStaff.StatoInvito.PENDING
    );
  }

  public List<InvitoStaff> ottieniInvitiPendingByHackathon(Long hackathonId) {
    return invitoStaffRepository.findByHackathonIdAndStato(
      hackathonId,
      InvitoStaff.StatoInvito.PENDING
    );
  }
}
