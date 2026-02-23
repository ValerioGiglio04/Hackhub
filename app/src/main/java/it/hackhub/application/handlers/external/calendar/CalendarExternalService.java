package it.hackhub.application.handlers.external.calendar;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Interfaccia per servizi esterni di calendario.
 * Definisce il contratto per la creazione e gestione di eventi di calendario.
 */
public interface CalendarExternalService {

  /**
   * Crea un evento di calendario e restituisce il link di accesso.
   *
   * @param titoloEvento Titolo dell'evento
   * @param descrizione Descrizione dettagliata dell'evento
   * @param data Data dell'evento
   * @param orario Orario di inizio dell'evento
   * @param emailUtente Email dell'utente (opzionale)
   * @return Link di accesso all'evento calendario
   * @throws RuntimeException in caso di errore durante la creazione
   */
  String creaEventoVideoconferenza(
    String titoloEvento,
    String descrizione,
    LocalDate data,
    LocalTime orario,
    String emailUtente
  );

  /**
   * Verifica la disponibilità di uno slot temporale.
   *
   * @param data Data da verificare
   * @param orario Orario da verificare
   * @return true se lo slot è disponibile, false altrimenti
   */
  boolean verificaDisponibilita(LocalDate data, LocalTime orario);

  /**
   * Cancella un evento di calendario.
   *
   * @param linkEventoCalendar Link dell'evento da cancellare
   * @throws RuntimeException in caso di errore durante la cancellazione
   */
  void cancellaEvento(String linkEventoCalendar);
}
