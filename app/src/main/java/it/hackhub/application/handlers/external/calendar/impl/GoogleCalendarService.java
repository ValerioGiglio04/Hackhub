package it.hackhub.application.handlers.external.calendar.impl;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import it.hackhub.application.exceptions.CalendarConflictException;
import it.hackhub.application.exceptions.PastDateException;
import it.hackhub.application.handlers.external.calendar.CalendarExternalService;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Integrazione con Google Calendar API (eventi + Google Meet).
 * Usa Service Account (file JSON). Per Google Meet con SA può essere necessario
 * Domain-Wide Delegation + google.calendar.impersonate.user.
 */
@Service
public class GoogleCalendarService implements CalendarExternalService {

  private static final String APPLICATION_NAME = "HackHub Calendar Service";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final DateTimeFormatter RFC3339_MILLIS =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
  private static final DateTimeFormatter RFC3339 =
    DateTimeFormatter.ISO_OFFSET_DATE_TIME;

  @Value("${google.calendar.calendarId}")
  private String calendarId;

  @Value("${google.credentials.file:}")
  private String credentialsFilePath;

  @Value("${google.calendar.impersonate.user:}")
  private String impersonateUser;

  private volatile GoogleCredentials credentials;
  private volatile NetHttpTransport httpTransport;
  private volatile String serviceAccountEmail;

  private NetHttpTransport getHttpTransport() throws Exception {
    if (httpTransport == null) {
      synchronized (this) {
        if (httpTransport == null) {
          httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        }
      }
    }
    return httpTransport;
  }

  private InputStream openCredentialsStream(String path) throws Exception {
    if (path.startsWith("classpath:")) {
      String resource = path
        .substring("classpath:".length())
        .trim()
        .replaceFirst("^/+", "");
      InputStream is = getClass()
        .getClassLoader()
        .getResourceAsStream(resource);
      if (is == null) {
        throw new IllegalArgumentException(
          "Risorsa classpath non trovata: " + resource
        );
      }
      return is;
    }
    return new FileInputStream(path);
  }

  private GoogleCredentials getCredentials(final NetHttpTransport HTTP_TRANSPORT) {
    if (credentialsFilePath == null || credentialsFilePath.isBlank()) {
      return null;
    }
    if (credentials != null) {
      try {
        credentials.refresh();
        return credentials;
      } catch (IOException e) {
        logGoogleApiError("Errore refresh credenziali", e);
      }
    }
    synchronized (this) {
      if (credentials != null) {
        return credentials;
      }
      String path = credentialsFilePath.trim();
      try (InputStream is = openCredentialsStream(path)) {
        GoogleCredentials creds = GoogleCredentials
          .fromStream(is)
          .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

        if (creds instanceof ServiceAccountCredentials) {
          serviceAccountEmail =
            ((ServiceAccountCredentials) creds).getClientEmail();
        }

        if (impersonateUser != null && !impersonateUser.isBlank()) {
          if (creds instanceof ServiceAccountCredentials) {
            creds =
              ((ServiceAccountCredentials) creds).createDelegated(
                impersonateUser.trim()
              );
          }
        }

        credentials = creds;
        return credentials;
      } catch (Exception e) {
        logGoogleApiError("Errore caricamento credenziali Google", e);
        return null;
      }
    }
  }

  private Calendar getCalendarClient() throws Exception {
    NetHttpTransport transport = getHttpTransport();
    GoogleCredentials creds = getCredentials(transport);
    if (creds == null) {
      return null;
    }
    return new Calendar.Builder(
      transport,
      JSON_FACTORY,
      new HttpCredentialsAdapter(creds)
    )
      .setApplicationName(APPLICATION_NAME)
      .build();
  }

  private boolean isCalendarAvailable() {
    try {
      return getCalendarClient() != null;
    } catch (Exception e) {
      return false;
    }
  }

  private void logGoogleApiError(String contesto, Exception e) {
    if (e instanceof GoogleJsonResponseException) {
      GoogleJsonResponseException gj = (GoogleJsonResponseException) e;
      GoogleJsonError details = gj.getDetails();
      if (details != null && details.getErrors() != null) {
        for (GoogleJsonError.ErrorInfo err : details.getErrors()) {
          System.err.println(
            "[Google Calendar API] " + contesto + " - " + err.getMessage()
          );
        }
      }
    } else if (e instanceof HttpResponseException) {
      HttpResponseException he = (HttpResponseException) e;
      System.err.println(
        "[Google Calendar API] " + contesto + " - HTTP " + he.getStatusCode()
      );
    }
    System.err.println(
      "[Google Calendar API] " + contesto + " - " + e.getClass().getSimpleName() + ": " + e.getMessage()
    );
  }

  private Event createEventWithConference(
    String titolo,
    ZonedDateTime startTime,
    ZonedDateTime endTime,
    boolean includeConference
  ) {
    String startRfc3339 = startTime.format(RFC3339_MILLIS);
    String endRfc3339 = endTime.format(RFC3339_MILLIS);
    String zoneId = startTime.getZone().getId();

    Event event = new Event()
      .setSummary(titolo)
      .setStart(
        new EventDateTime()
          .setDateTime(new DateTime(startRfc3339))
          .setTimeZone(zoneId)
      )
      .setEnd(
        new EventDateTime()
          .setDateTime(new DateTime(endRfc3339))
          .setTimeZone(zoneId)
      );

    if (includeConference) {
      String requestId = UUID.randomUUID().toString();
      event.setConferenceData(
        new com.google.api.services.calendar.model.ConferenceData()
          .setCreateRequest(
            new com.google.api.services.calendar.model.CreateConferenceRequest()
              .setRequestId(requestId)
          )
      );
    }

    return event;
  }

  private String creaEventoGoogleCalendar(
    String titolo,
    String descrizione,
    ZonedDateTime startTime,
    ZonedDateTime endTime,
    String email
  ) {
    try {
      Calendar calendarService = getCalendarClient();
      if (calendarService == null) {
        return null;
      }

      Event event = createEventWithConference(
        titolo,
        startTime,
        endTime,
        false
      );

      Event createdEvent = calendarService
        .events()
        .insert(calendarId, event)
        .execute();

      if (createdEvent.getId() == null) return null;

      String link = createdEvent.getHtmlLink();
      if (link == null || link.isBlank()) {
        Event fullEvent = calendarService
          .events()
          .get(calendarId, createdEvent.getId())
          .execute();
        link = fullEvent != null ? fullEvent.getHtmlLink() : null;
      }
      if (link == null || link.isBlank()) {
        String eid = createdEvent.getId() + " " + calendarId;
        String encoded = Base64
          .getUrlEncoder()
          .withoutPadding()
          .encodeToString(eid.getBytes(StandardCharsets.UTF_8));
        link = "https://calendar.google.com/calendar/event?eid=" + encoded;
      }
      return link;
    } catch (Exception e) {
      logGoogleApiError("Errore creazione evento Google Calendar", e);
      return null;
    }
  }

  @Override
  public String creaEventoVideoconferenza(
    String titoloEvento,
    String descrizione,
    LocalDate data,
    LocalTime orario,
    String emailUtente
  ) {
    try {
      ZonedDateTime startTime = data
        .atTime(orario)
        .atZone(ZoneId.systemDefault());
      ZonedDateTime endTime = startTime.plusHours(1);

      verificaDisponibilitaRangeEsteso(data, orario);

      String eventLink = creaEventoGoogleCalendar(
        titoloEvento,
        descrizione,
        startTime,
        endTime,
        emailUtente
      );

      if (eventLink != null) {
        return eventLink;
      }
      throw new RuntimeException(
        "Errore nella creazione evento Google Calendar"
      );
    } catch (PastDateException | CalendarConflictException e) {
      throw e;
    } catch (Exception e) {
      logGoogleApiError("Errore nella creazione evento videoconferenza", e);
      throw new RuntimeException(
        "Errore nella creazione evento Google Calendar"
      );
    }
  }

  @Override
  public boolean verificaDisponibilita(LocalDate data, LocalTime orario) {
    try {
      LocalDate oggi = LocalDate.now();
      LocalTime oraCorrente = LocalTime.now();

      if (
        data.isBefore(oggi) ||
        (data.isEqual(oggi) && orario.isBefore(oraCorrente))
      ) {
        return false;
      }
      if (
        orario.isBefore(LocalTime.of(8, 0)) ||
        orario.isAfter(LocalTime.of(20, 0))
      ) {
        return false;
      }

      ZonedDateTime startTime = data
        .atTime(orario)
        .atZone(ZoneId.systemDefault());
      ZonedDateTime endTime = startTime.plusHours(1);

      if (!isCalendarAvailable()) {
        return true;
      }
      return !controllaEventiGoogleCalendar(startTime, endTime);
    } catch (Exception e) {
      logGoogleApiError("Errore verifica disponibilità Google Calendar", e);
      return false;
    }
  }

  @Override
  public void cancellaEvento(String linkEventoCalendar) {
    try {
      if (
        linkEventoCalendar == null ||
        !linkEventoCalendar.contains("calendar.google.com")
      ) {
        return;
      }
      Calendar calendarService = getCalendarClient();
      if (calendarService == null) {
        return;
      }
      String eventId = findEventIdByEventLink(
        calendarService,
        linkEventoCalendar
      );
      if (eventId != null) {
        calendarService.events().delete(calendarId, eventId).execute();
      }
    } catch (Exception e) {
      logGoogleApiError("Errore cancellazione evento Google Calendar", e);
    }
  }

  private String findEventIdByEventLink(
    Calendar calendarService,
    String eventLink
  ) throws Exception {
    String normalizedLink = eventLink.trim();
    if (!normalizedLink.startsWith("http")) {
      normalizedLink =
        "https://www.google.com/calendar/event?eid=" + normalizedLink;
    }
    final String linkToFind = normalizedLink;

    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime from = now.minusDays(30);
    ZonedDateTime to = now.plusYears(1);

    DateTime timeMin = new DateTime(from.format(RFC3339));
    DateTime timeMax = new DateTime(to.format(RFC3339));

    Calendar.Events.List list = calendarService
      .events()
      .list(calendarId)
      .setTimeMin(timeMin)
      .setTimeMax(timeMax)
      .setSingleEvents(true)
      .setOrderBy("startTime");

    String pageToken = null;
    do {
      if (pageToken != null) {
        list.setPageToken(pageToken);
      }
      Events events = list.execute();
      List<Event> items = events.getItems();
      List<Event> itemList = items != null ? items : Collections.emptyList();
      Optional<String> match = itemList
        .stream()
        .filter(e -> linkToFind.equals(e.getHtmlLink()))
        .findFirst()
        .map(Event::getId);
      if (match.isPresent()) {
        return match.get();
      }
      pageToken = events.getNextPageToken();
    } while (pageToken != null);

    return null;
  }

  private boolean controllaEventiGoogleCalendar(
    ZonedDateTime startTime,
    ZonedDateTime endTime
  ) {
    try {
      Calendar calendarService = getCalendarClient();
      if (calendarService == null) {
        return false;
      }
      DateTime timeMin = new DateTime(startTime.format(RFC3339));
      DateTime timeMax = new DateTime(endTime.format(RFC3339));

      Events result = calendarService
        .events()
        .list(calendarId)
        .setTimeMin(timeMin)
        .setTimeMax(timeMax)
        .setSingleEvents(true)
        .setOrderBy("startTime")
        .execute();

      List<Event> items = result.getItems();
      return items != null && !items.isEmpty();
    } catch (Exception e) {
      logGoogleApiError("Errore controllo eventi Google Calendar", e);
      return false;
    }
  }

  private boolean verificaDisponibilitaRangeEsteso(
    LocalDate data,
    LocalTime orario
  ) {
    try {
      LocalDate oggi = LocalDate.now();
      LocalTime oraCorrente = LocalTime.now();

      if (data.isBefore(oggi)) {
        throw new PastDateException(
          "Impossibile creare un evento in una data passata: " +
            data +
            " (oggi è " +
            oggi +
            ")"
        );
      }

      if (data.isEqual(oggi) && orario.isBefore(oraCorrente)) {
        throw new PastDateException(
          "Impossibile creare un evento in un orario passato: " +
            orario +
            " di oggi (ora attuale: " +
            oraCorrente +
            ")"
        );
      }

      ZonedDateTime requestedStartTime = data
        .atTime(orario)
        .atZone(ZoneId.systemDefault());
      ZonedDateTime requestedEndTime = requestedStartTime.plusHours(1);
      ZonedDateTime searchStartTime = requestedStartTime.minusHours(1);
      ZonedDateTime searchEndTime = requestedEndTime.plusHours(1);

      if (!isCalendarAvailable()) {
        return true;
      }

      boolean hasConflicts = controllaEventiGoogleCalendar(
        searchStartTime,
        searchEndTime
      );
      if (hasConflicts) {
        throw new CalendarConflictException(
          "Impossibile creare l'evento: nel calendario è già presente un altro evento nel range di un'ora dalla data e orario indicati (" +
            orario +
            " del " +
            data +
            "). Scegliere uno slot libero."
        );
      }

      return true;
    } catch (PastDateException | CalendarConflictException e) {
      throw e;
    } catch (Exception e) {
      logGoogleApiError(
        "Errore verifica disponibilità range esteso Google Calendar",
        e
      );
      return false;
    }
  }
}
