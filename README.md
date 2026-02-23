# HackHub

HackHub è una piattaforma backend sviluppata in Java con Spring Boot per la gestione completa di Hackathon. Questo progetto è stato realizzato per il corso di Ingegneria del Software (IdS) presso l'Università di Camerino (Unicam).

## 📋 Descrizione

HackHub permette di gestire l'intero ciclo di vita di un hackathon, dalla creazione alla premiazione, includendo la gestione di team, partecipanti, giudici e mentori. Il sistema supporta diverse fasi (iscrizione, svolgimento, valutazione, premiazione) e fornisce funzionalità per la sottomissione dei progetti, la loro valutazione, le richieste di supporto ai mentori e l'integrazione con servizi esterni (PayPal per i premi, Google Calendar per le call di supporto).

## 🚀 Tecnologie Utilizzate

- **Java 21**: Linguaggio di programmazione principale.
- **Spring Boot 3.2**: Framework per lo sviluppo dell'applicazione backend.
- **Spring Data JPA**: Per la persistenza dei dati.
- **Spring Security + JWT**: Autenticazione e autorizzazione basata su token.
- **H2 Database**: Database su file per sviluppo e testing (`./data/hackhubdb`).
- **Gradle**: Strumento di build e gestione delle dipendenze.
- **SpringDoc OpenAPI**: Documentazione API (Swagger UI).
- **PayPal SDK** (configurabile): Integrazione per il pagamento dei premi in denaro (sandbox).
- **Google Calendar API** (opzionale): Per la creazione di appuntamenti/call di supporto.

## ✨ Funzionalità Principali

- **Gestione Utenti**: Registrazione, login/logout, ruoli differenziati (Autenticato, Organizzatore, Mentore, Giudice).
- **Gestione Hackathon**: Creazione, modifica e gestione degli stati dell'evento (In Attesa, In Iscrizione, In Corso, In Valutazione, In Premiazione, Concluso, Annullato).
- **Gestione Team**: Creazione squadre, inviti tra utenti, accettazione/rifiuto inviti, abbandono team, nomina capo, iscrizione team all'hackathon.
- **Inviti Staff**: Invito di mentori e giudici agli hackathon da parte degli organizzatori; gestione (accetta/rifiuta) da parte dello staff.
- **Sottomissioni**: Caricamento e aggiornamento dei progetti da parte dei team (supporto per link esterni, es. GitHub).
- **Valutazioni**: Sistema di valutazione delle sottomissioni da parte dei giudici.
- **Richieste di Supporto**: I team possono creare richieste di supporto; i mentori possono visualizzarle e proporre call (integrazione con Google Calendar).
- **Segnalazioni Violazioni**: Possibilità di segnalare violazioni nel contesto del supporto.
- **Scheduler Automatico**: Avanzamento automatico delle fasi dell'hackathon (iscrizione, svolgimento) basato sulle date.
- **Premi in Denaro**: Gestione del premio per il vincitore con integrazione PayPal Sandbox.

## 🛠️ Architettura e Pattern

Il progetto segue un'architettura a livelli (Layered Architecture) con una separazione chiara tra:

- **Presentation**: Controller REST, gestione eccezioni globali.
- **Application**: Handlers (logica di business), DTO, Mapper, interfacce Repository, eccezioni di dominio.
- **Core**: Entità del dominio (Utente, Team, Hackathon, Sottomissione, Valutazione, RichiestaSupporto, ecc.).
- **Infrastructure**: Sicurezza (JWT, UserDetails), implementazioni servizi esterni (PayPal, Google Calendar).

Pattern e pratiche utilizzate includono:

- **Handler/Service**: Logica di business concentrata negli handler iniettati nei controller.
- **DTO e Mapper**: Separazione tra modelli di dominio e modelli di scambio (request/response).
- **Repository (Spring Data JPA)**: Accesso ai dati in modo dichiarativo.
- **JWT**: Token per autenticazione stateless; supporto logout con invalidazione token (blacklist in memoria).
- **StandardResponse**: Risposte API omogenee con successo/errore e messaggio.

## 📦 Installazione e Avvio

### Prerequisiti

- JDK 17 installato.
- Gradle (opzionale, incluso nel wrapper `gradlew`).

### Passaggi

1.  Clona il repository:
    ```bash
    git clone <url-repository>
    ```
2.  Naviga nella cartella del progetto (modulo applicazione):
    ```bash
    cd Hackhub/app
    ```
3.  Avvia l'applicazione con Gradle Wrapper:
    - **Windows**:
      ```bash
      .\gradlew.bat bootRun
      ```
    - **Linux/Mac**:
      ```bash
      chmod +x gradlew
      ./gradlew bootRun
      ```

L'applicazione si avvierà sulla porta **8080**.

## 📚 Documentazione API

Una volta avviata l'applicazione, puoi accedere alla documentazione interattiva delle API (Swagger UI) al seguente indirizzo:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## 🗄️ Database Console

Il database H2 è persistente su file: `./data/hackhubdb` (relativo alla directory di esecuzione, tipicamente `app`).

Per ispezionare il database tramite H2 Console:

- URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL: `jdbc:h2:file:./data/hackhubdb`
- Username: `sa`
- Password: `password`

---

## 🧪 Sequenza di Test Manuale

Di seguito è riportata una sequenza di operazioni per testare manualmente il flusso principale dell'applicazione.

### 1. Popolamento Database (SQL)

È disponibile uno script SQL per inserire utenti, team, hackathon, staff, iscrizioni e sottomissioni di test:

**`scripts/data-test-users.sql`**

> **Importante**: Eseguire solo su database con schema già creato (avviare prima l'applicazione almeno una volta). La password per tutti gli utenti di test è **`test123`**.

**Come eseguire lo script:**

1. Avvia l'applicazione (per creare lo schema e il file DB).
2. Apri la [H2 Console](http://localhost:8080/h2-console) e connettiti (JDBC URL: `jdbc:h2:file:./data/hackhubdb`, user: `sa`, password: `password`).
3. Copia e incolla il contenuto di `scripts/data-test-users.sql` nella finestra SQL e esegui.

**Contenuto sintetico dello script:**

- **Utenti (id 1..10)**: 1 organizzatore, 2 mentori, 2 giudici, 2 capi team (Marco Ferrari, Francesca Esposito), 4 membri.
- **Team**: CodeCrushers (capo id=6), TechInnovators (capo id=9).
- **Hackathon**: "HackHub Innovation Challenge 2024" in stato IN_ISCRIZIONE.
- **Staff, iscrizioni e sottomissioni** già configurati per i test.

Dopo il popolamento puoi fare login con ad esempio: `organizzatore@hackhub.com`, `capoteam1@hackhub.com`, `mentore1@hackhub.com`, `giudice1@hackhub.com` e password `test123`.

### 2. Flusso Operativo (Chiamate API)

> **Importante**: Dopo ogni login, copia il `token` dalla risposta (campo `data.token`) e utilizzalo nell'header per le chiamate successive.  
> Header: `Authorization: Bearer <TOKEN>`

#### A. Autenticazione (Login)

**Login Organizzatore (Mario Rossi)**  
`POST http://localhost:8080/api/autenticazione/login`

```json
{
  "email": "organizzatore@hackhub.com",
  "password": "test123"
}
```

**Login Mentore (Laura Bianchi)**  
`POST http://localhost:8080/api/autenticazione/login`

```json
{
  "email": "mentore1@hackhub.com",
  "password": "test123"
}
```

**Login Giudice (Paolo Neri)**  
`POST http://localhost:8080/api/autenticazione/login`

```json
{
  "email": "giudice1@hackhub.com",
  "password": "test123"
}
```

**Login Capo Team (Marco Ferrari - Utente 6)**  
`POST http://localhost:8080/api/autenticazione/login`

```json
{
  "email": "capoteam1@hackhub.com",
  "password": "test123"
}
```

**Login Membro Team (Sara Romano - Utente 7)**  
`POST http://localhost:8080/api/autenticazione/login`

```json
{
  "email": "membro1_1@hackhub.com",
  "password": "test123"
}
```

#### B. Gestione Team

**1. Creazione Team (Utente 6 – capo)**  
`POST http://localhost:8080/api/team/crea`  
_Header Auth: Token Utente 6 (capoteam1)_

```json
{
  "nome": "Nuovo Team",
  "capoId": 6,
  "utentiDaInvitareIds": []
}
```

_Nota: Se l’utente 6 è già capo di un team (CodeCrushers), usa quel team per i passi successivi. Altrimenti copia l’`id` del team creato (es. `TEAM_ID`)._

**2. Invito a team (da Capo a Utente 7)**  
`POST http://localhost:8080/api/inviti/invia-invito`  
_Header Auth: Token Capo Team_

```json
{
  "teamId": 1,
  "utenteInvitatoId": 7
}
```

_Nota: Copia l’`id` dell’invito dalla risposta (es. `INVITO_ID`)._

**3. Accettazione invito (Utente 7)**  
`POST http://localhost:8080/api/inviti/{invitoId}/gestisci-invito`  
_Header Auth: Token Utente 7_

```json
{
  "azione": "ACCETTA"
}
```

#### C. Gestione Hackathon

**1. Creazione Hackathon (Organizzatore)**  
`POST http://localhost:8080/api/hackathon`  
_Header Auth: Token Organizzatore_

_Nota: Assicurati che le date siano coerenti (iscrizioni, inizio/fine, scadenza sottomissioni) per permettere il flusso di test._

```json
{
  "nome": "Hackathon Test 2025",
  "regolamento": "Regolamento ufficiale...",
  "inizioIscrizioni": "2025-02-20T00:00:00",
  "scadenzaIscrizioni": "2025-03-01T23:59:59",
  "dataInizio": "2025-03-05T09:00:00",
  "dataFine": "2025-03-07T18:00:00",
  "scadenzaSottomissioni": "2025-03-07T17:00:00",
  "luogo": "Roma / Online",
  "premio": 1000.0,
  "maxTeamSize": 4
}
```

_Nota: Copia l’`id` dell’hackathon (es. `HACKATHON_ID`)._

**2. Invito staff (Organizzatore)**  
`POST http://localhost:8080/api/hackathon/invita-staff`  
_Header Auth: Token Organizzatore_

```json
{
  "hackathonId": 1,
  "utenteId": 2
}
```

_(Ripeti per altri mentori/giudici se necessario; i mentori/giudici accettano da `GET /api/staff-inviti/ricevuti` e `POST /api/staff-inviti/{invitoId}/gestisci-invito`.)_

**3. Iscrizione team all’hackathon (Capo Team)**  
`POST http://localhost:8080/api/team/iscrivi-hackathon`  
_Header Auth: Token Capo Team_

```json
{
  "teamId": 1,
  "hackathonId": 1
}
```

#### D. Svolgimento e Valutazione

**1. Sottomissione progetto (membro del team)**  
`POST http://localhost:8080/api/sottomissioni/invia`  
_Header Auth: Token Capo Team o Membro_

_Nota: L’hackathon deve essere in fase IN_CORSO (tra dataInizio e dataFine). Lo scheduler aggiorna gli stati; in sviluppo puoi usare date che ricadono “in corso” nel momento del test._

```json
{
  "teamId": 1,
  "hackathonId": 1,
  "linkProgetto": "https://github.com/mio-team/progetto"
}
```

_Nota: Copia l’`id` della sottomissione (es. `SOTTOMISSIONE_ID`)._

**2. Valutazione (Giudice)**  
`POST http://localhost:8080/api/valutazioni/crea`  
_Header Auth: Token Giudice_

_Nota: Il giudice deve essere assegnato all’hackathon della sottomissione. L’hackathon deve essere in fase di valutazione._

```json
{
  "sottomissioneId": 1,
  "hackathonId": 1,
  "punteggio": 85,
  "commento": "Ottimo lavoro, codice pulito."
}
```

_(Il campo `giudiceId` viene impostato automaticamente dal backend.)_

**3. Proclama vincitore (Organizzatore)**  
`POST http://localhost:8080/api/hackathon/vincitore`  
_Header Auth: Token Organizzatore_

```json
{
  "hackathonId": 1,
  "teamId": 1
}
```

#### E. Supporto (Mentoring)

**1. Richiesta supporto (membro team)**  
`POST http://localhost:8080/api/support/crea-richiesta`  
_Header Auth: Token Capo Team o Membro_

```json
{
  "teamId": 1,
  "hackathonId": 1,
  "descrizione": "Abbiamo bisogno di aiuto con l'integrazione API"
}
```

_Nota: Copia l’`id` della richiesta (es. `RICHIESTA_ID`)._

**2. Visualizza richieste (Mentore)**  
`GET http://localhost:8080/api/support/richieste`  
_Header Auth: Token Mentore_

Oppure per hackathon:  
`GET http://localhost:8080/api/support/richieste/hackathon/1`

**3. Proponi chiamata (Mentore)**  
`POST http://localhost:8080/api/calendar/proponi-chiamata`  
_Header Auth: Token Mentore_

```json
{
  "idTeam": 1,
  "idHackaton": 1,
  "idMentore": 2,
  "richiestaSupportoId": 1,
  "dataChiamata": "2025-03-06",
  "orario": "15:00:00"
}
```

---

## 🔐 Autenticazione

Le API (tranne registrazione e login) richiedono autenticazione JWT. Dopo il login, usa il token ricevuto nel campo `data.token` della risposta nell'header:

```http
Authorization: Bearer <TOKEN>
```

- **Registrazione**: `POST /api/autenticazione/registrazione`
- **Login**: `POST /api/autenticazione/login` (restituisce `data.token`)
- **Utente corrente**: `GET /api/autenticazione/me`
- **Logout**: `POST /api/autenticazione/logout`

## 👥 Autori

- Progetto HackHub – Ingegneria del Software, Università di Camerino.
